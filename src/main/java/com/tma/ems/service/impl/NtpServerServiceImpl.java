package com.tma.ems.service.impl;

import com.tma.ems.constant.Command;
import com.tma.ems.constant.Message;
import com.tma.ems.entity.Device;
import com.tma.ems.entity.Ntpaddress;
import com.tma.ems.entity.Ntpserver;
import com.tma.ems.exception.BadRequestException;
import com.tma.ems.exception.ConflictException;
import com.tma.ems.exception.NotFoundException;
import com.tma.ems.parser.CommandParser;
import com.tma.ems.repository.DeviceRepository;
import com.tma.ems.repository.NtpAddressRepository;
import com.tma.ems.repository.NtpServerRepository;
import com.tma.ems.service.NtpServerService;
import com.tma.ems.utils.SshUtils;
import com.tma.ems.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * solve logic related to ntp server
 */
@Service
@RequiredArgsConstructor
public class NtpServerServiceImpl implements NtpServerService {
    private final NtpServerRepository ntpServerRepo;
    private final NtpAddressRepository ntpAddressRepo;
    private final DeviceRepository deviceRepo;

    @Override
    public Ntpserver getNtpserverByDeviceId(Long idDevice) {
        //check id device
        if(existNtpServerWithDevice(idDevice)){
            return ntpServerRepo.findNtpserverByDevice_Id(idDevice);
        }else {
            throw new NotFoundException(Message.NON_EXIST_NTPSERVER);
        }
    }

    @Override
    public Ntpaddress addNtpserver(Long idDevice, Ntpaddress ntpaddress) {
        if (existNtpServerWithDevice(idDevice)) {
            //check if ip of ntp address is valid
            if (ValidationUtils.isValidIp(ntpaddress.getAddress())) {
                //get ntp server and device
                Ntpserver ntpserver = ntpServerRepo.findNtpserverByDevice_Id(idDevice);
                Device device = deviceRepo.findDeviceById(idDevice);
                // check if ipaddress is duplicated or not
                if (ntpAddressRepo.existsByNtpserver_IdAndAddress(ntpserver.getId(), ntpaddress.getAddress())) {
                    throw new ConflictException(Message.DUPLICATE_NTP);
                } else {
                    //create and execute command
                    String command = Command.NTP_ADD.replace("ip_address", ntpaddress.getAddress());
                    String output = SshUtils.executeCommand(device, command);
                    //check output is success or erorr
                    if (!CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                        ntpaddress.setNtpserver(ntpserver);
                        return ntpAddressRepo.save(ntpaddress);
                    } else {
                        throw new BadRequestException(CommandParser.formatOutput(output));
                    }
                }
            } else {
                throw new BadRequestException(Message.INVALID_IP);
            }

        }else {
            throw new NotFoundException(Message.NON_EXIST_NTPSERVER);
        }
    }

    @Override
    public void deleteNtpserver(Long idDevice, String address) {
        if(existNtpServerWithDevice(idDevice)) {
            Device device=deviceRepo.findDeviceById(idDevice);
            Ntpserver ntpserver = ntpServerRepo.findNtpserverByDevice_Id(idDevice);
            //check if exist address in ntp server
            if (ntpAddressRepo.existsByNtpserver_IdAndAddress(ntpserver.getId(), address)) {
                //create and execute command
                String command = Command.NTP_DELETE.replace("ip_address", address);
                String output = SshUtils.executeCommand(device, command);
                //check output is success or error
                if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                    output = CommandParser.formatOutput(output);
                    throw new BadRequestException(output);
                } else {
                    ntpAddressRepo.deleteByAddress(address);
                }
            }
        }else {
            throw new NotFoundException(Message.NON_EXIST_NTPSERVER);
        }
    }

    /**
     * check if exist ntp server with device given id
     */
    public boolean existNtpServerWithDevice(Long idDevice){
        //check if id device is existed
        if (deviceRepo.existsById(idDevice)) {
            Device device = deviceRepo.findDeviceById(idDevice);
            if (device.isConnected()) {
                //check if exist ntp server of this device
                return ntpServerRepo.existsByDevice_Id(idDevice);
            } else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }


}
