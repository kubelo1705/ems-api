package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Ntpaddress;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.exception.BadRequestException;
import com.example.managedevices.exception.ConflictException;
import com.example.managedevices.exception.NotFoundException;
import com.example.managedevices.parser.CommandParser;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.NtpAddressRepository;
import com.example.managedevices.repository.NtpServerRepository;
import com.example.managedevices.service.NtpServerService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.ValidationUtils;
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
        if (deviceRepo.existsById(idDevice)) {
            if(deviceRepo.existsByCredential_IdAndConnected(idDevice,true)) {
                Ntpserver ntpserver = ntpServerRepo.findNtpserverByDevice_Id(idDevice);
                if (ntpserver != null) {
                    return ntpserver;
                }
                throw new NotFoundException(Message.NON_EXIST_NTPSERVER + " WITH DEVICE ID=" + idDevice);
            }else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        }else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public Ntpaddress addNtpserver(Long idDevice, Ntpaddress ntpaddress) {
        if (deviceRepo.existsById(idDevice)) {
            if(deviceRepo.existsByCredential_IdAndConnected(idDevice,true)) {
                if (ntpServerRepo.existsByDevice_Id(idDevice)) {
                    if (ValidationUtils.isValidIp(ntpaddress.getAddress())) {
                        Ntpserver ntpserver = ntpServerRepo.findNtpserverByDevice_Id(idDevice);
                        Device device = deviceRepo.findDeviceById(idDevice);

                        if (ntpAddressRepo.existsByNtpserver_IdAndAddress(ntpserver.getId(), ntpaddress.getAddress())) {
                            throw new ConflictException(Message.DUPLICATE_NTP);
                        } else {
                            String command = Command.ADD_NTP.replace("ip_address", ntpaddress.getAddress());
                            String output = CommandUtils.execute(device, device.getCredential(), command);
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

                } else {
                    throw new NotFoundException(Message.NON_EXIST_NTPSERVER);
                }
            }else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

//    @Override
//    public Ntpserver updateNtpserver(Ntpserver ntpServer, Long id) {
//        Ntpserver ntpServerUpdate=ntpServerRepo.findNtpServerById(id);
//        if(ntpServerUpdate!=null){
//            if(EntityValidator.isValidIp(ntpServer.getServerAddress())) {
//                ntpServerUpdate.setServerAddress(ntpServer.getServerAddress());
//                ntpServerUpdate.setState(ntpServer.isState());
//                return ntpServerRepo.save(ntpServerUpdate);
//            }else{
//                throw new EmsException(Message.INVALID_IP);
//            }
//        }else{
//            throw new EmsException(Message.NON_EXIST_NTPSERVER);
//        }
//    }

    @Override
    public void deleteNtpserver(Long idDevice, String address) {
        if (deviceRepo.existsById(idDevice)) {
            Device device = deviceRepo.findDeviceById(idDevice);
            if (device.isConnected()) {
                if (ntpServerRepo.existsByDevice_Id(idDevice)) {
                    Ntpserver ntpserver = ntpServerRepo.findNtpserverByDevice_Id(idDevice);
                    if (ntpAddressRepo.existsByNtpserver_IdAndAddress(ntpserver.getId(), address)) {
                        String command = Command.DELETE_NTP.replace("ip_address", address);
                        String output = CommandUtils.execute(device, device.getCredential(), command);
                        if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                            output = CommandParser.formatOutput(output);
                            throw new BadRequestException(output);
                        } else {
                            ntpAddressRepo.deleteByAddress(address);
                        }
                    }
                } else {
                    throw new NotFoundException(Message.NON_EXIST_NTPSERVER);
                }
            } else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }


}
