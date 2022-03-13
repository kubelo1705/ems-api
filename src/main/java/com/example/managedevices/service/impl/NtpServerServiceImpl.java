package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.NtpServer;
import com.example.managedevices.exception.EmsException;
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

    @Override
    public List<NtpServer> getAllNtpservers() {
        return ntpServerRepo.findAll();
    }

    @Override
    public NtpServer addNtpserver(NtpServer ntpServer) {
        if(EntityValidator.isValidIp(ntpServer.getServerAddress()))
            return ntpServerRepo.save(ntpServer);
        else
            throw new EmsException(Message.INVALID_IP);
    }

    @Override
    public NtpServer updateNtpserver(NtpServer ntpServer,Long id) {
        NtpServer ntpServerUpdate=ntpServerRepo.findNtpServerById(id);
        if(ntpServerUpdate!=null){
            if(EntityValidator.isValidIp(ntpServer.getServerAddress())) {
                ntpServerUpdate.setServerAddress(ntpServer.getServerAddress());
                ntpServerUpdate.setState(ntpServer.isState());
                return ntpServerRepo.save(ntpServerUpdate);
            }else{
                throw new EmsException(Message.INVALID_IP);
            }
        }else{
            throw new EmsException(Message.NON_EXIST_NTPSERVER);
        }
    }

    @Override
    public void deleteNtpserver(Long id) {
        NtpServer ntpServer=ntpServerRepo.findNtpServerById(id);
        if(ntpServer!=null){
            ntpServerRepo.delete(ntpServer);
        }else{
            throw new EmsException(Message.NON_EXIST_NTPSERVER);
        }
    }


}
