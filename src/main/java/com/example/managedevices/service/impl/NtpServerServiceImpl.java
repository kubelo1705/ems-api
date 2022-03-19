package com.example.managedevices.service.impl;

import com.example.managedevices.constant.BaseCommand;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Ntpaddress;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.NtpAddressRepository;
import com.example.managedevices.repository.NtpServerRepository;
import com.example.managedevices.service.NtpServerService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.OutputUtils;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NtpServerServiceImpl implements NtpServerService {
    private final NtpServerRepository ntpServerRepo;
    private final NtpAddressRepository ntpAddressRepo;
    private final DeviceRepository deviceRepo;

    @Override
    public List<Ntpserver> getAllNtpservers() {
        return ntpServerRepo.findAll();
    }

    @Override
    public Ntpaddress addNtpserver(Long idDevice, Ntpaddress ntpaddress) {
        if (deviceRepo.existsByIdAndStatus(idDevice,true)) {
            if (ntpServerRepo.existsByDevice_Id(idDevice)) {
                if(EntityValidator.isValidIp(ntpaddress.getAddress())) {
                    Ntpserver ntpserver = ntpServerRepo.findNtpserverByDevice_Id(idDevice);
                    Device device = deviceRepo.findDeviceById(idDevice);

                    if (ntpAddressRepo.existsByNtpserver_IdAndAddress(ntpserver.getId(), ntpaddress.getAddress())) {
                        throw new EmsException(Message.DUPLICATE_NTP);
                    } else {
                        String command = BaseCommand.ADD_NTP.replace("ip_address", ntpaddress.getAddress());
                        String output=CommandUtils.execute(device,device.getCredential(),command);
                        if(!OutputUtils.isErrorOutput(device.getSerialNumber(),command,output)){
                            ntpaddress.setNtpserver(ntpserver);
                            return ntpAddressRepo.save(ntpaddress);
                        }else{
                            throw new EmsException(OutputUtils.formatOutput(output));
                        }
                    }
                }else {
                    throw new EmsException(Message.INVALID_IP);
                }

            } else {
                throw new EmsException(Message.NON_EXIST_NTPSERVER);
            }
        }else {
            throw new EmsException(Message.NON_EXIST_DEVICE);
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
    public void deleteNtpserver(Long idDevice,String address) {
        if(deviceRepo.existsById(idDevice)){
            Device device=deviceRepo.findDeviceById(idDevice);
            if(device.isStatus()){
            if(ntpServerRepo.existsByDevice_Id(idDevice)) {
                Ntpserver ntpserver = ntpServerRepo.findNtpserverByDevice_Id(idDevice);
                if (ntpAddressRepo.existsByNtpserver_IdAndAddress(ntpserver.getId(), address)) {
                    String command = BaseCommand.DELETE_NTP.replace("ip_address", address);
                    String output = CommandUtils.execute(device, device.getCredential(), command);
                    if (OutputUtils.isErrorOutput(device.getSerialNumber(), command, output)) {
                        output = OutputUtils.formatOutput(output);
                        throw new EmsException(output);
                    } else {
                        ntpAddressRepo.deleteByAddress(address);
                    }
                }
            }else {
                throw new EmsException(Message.HAVENT_CREATED_CONNECTION);
            }
            }else{
                throw new EmsException(Message.NON_EXIST_NTPSERVER);
            }
        }else {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        }
    }


}
