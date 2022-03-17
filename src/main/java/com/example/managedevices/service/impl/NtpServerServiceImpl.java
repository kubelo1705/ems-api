package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.NtpAddressRepository;
import com.example.managedevices.repository.NtpServerRepository;
import com.example.managedevices.service.NtpServerService;
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
    public Ntpserver addNtpserver(Ntpserver ntpServer) {
        try {
            if (ntpServerRepo.existsById(ntpServer.getId())) {
                ntpServer.getNtpaddresses().forEach(ntpAddress -> {
                    if(EntityValidator.isValidIp(ntpAddress.getAddress())){
                        if(!ntpAddressRepo.existsByNtpserver_IdAndAddress(ntpServer.getId(),ntpAddress.getAddress())){
                            ntpAddress.setStatus(false);
                            ntpAddress.setId(ntpServer.getId());
                            ntpAddressRepo.save(ntpAddress);
                        }else {
                            throw new EmsException(Message.DUPLICATE_NTP);
                        }
                    }else{
                        throw new EmsException(Message.INVALID_DATA);
                    }
                });
                throw new EmsException(Message.INVALID_IP);
            }else{
                throw new EmsException(Message.NON_EXIST_NTPSERVER);
            }
        }catch (Exception e){
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
    public void deleteNtpserver(Long id) {
        Ntpserver ntpServer=ntpServerRepo.findNtpServerById(id);
        if(ntpServer!=null){
            ntpServerRepo.delete(ntpServer);
        }else{
            throw new EmsException(Message.NON_EXIST_NTPSERVER);
        }
    }


}
