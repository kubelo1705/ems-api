package com.tma.ems.service.impl;

import com.tma.ems.constant.Message;
import com.tma.ems.entity.Port;
import com.tma.ems.exception.BadRequestException;
import com.tma.ems.exception.NotFoundException;
import com.tma.ems.repository.DeviceRepository;
import com.tma.ems.repository.PortRepository;
import com.tma.ems.service.PortService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
            if(deviceRepo.existsByIdAndConnected(id,true)){
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
