package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.entity.Port;
import com.example.managedevices.exception.BadRequestException;
import com.example.managedevices.exception.NotFoundException;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.PortRepository;
import com.example.managedevices.service.PortService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.MissingResourceException;

/**
 * solve logic related to port
 */
@Service
@RequiredArgsConstructor
public class PortServiceImpl implements PortService {
    private final PortRepository portRepo;
    private final DeviceRepository deviceRepo;

    @Override
    public List<Port> getPortByDeviceId(Long id) {
        if(deviceRepo.existsById(id)) {
            if(deviceRepo.existsByCredential_IdAndConnected(id,true)){
                List<Port> ports=portRepo.findPortsByDevice_Id(id);
                if(ports.isEmpty()) {
                    return portRepo.findPortsByDevice_Id(id);
                }else {
                    throw new NotFoundException(Message.NON_EXIST_PORT);
                }
            }else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        }else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

}
