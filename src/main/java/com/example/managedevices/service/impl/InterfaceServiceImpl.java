package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.exception.DeviceException;
import com.example.managedevices.repo.DeviceRepo;
import com.example.managedevices.repo.InterfaceRepo;
import com.example.managedevices.service.InterfaceService;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterfaceServiceImpl implements InterfaceService {
    private final InterfaceRepo interfaceRepo;
    private final DeviceRepo deviceRepo;

    @Override
    public List<Interface> getAllInterface() {
        return interfaceRepo.findAll();
    }

    @Override
    public List<Interface> getInterfacesByDeviceId(Long id) {
        return interfaceRepo.findInterfaceByDevice_Id(id);
    }

    @Override
    public Interface addInterface(Interface interfaceAdd) {
        return interfaceRepo.save(interfaceAdd);
    }

    @Override
    public boolean checkNetmask(Interface interfaceCheck) {
        return EntityValidator.isValidNetmask(interfaceCheck.getNetmask());
    }

    @Override
    public boolean checkIpAddress(Interface interfaceCheck){
        return EntityValidator.isValidIp(interfaceCheck.getIpAddress());
    }

    @Override
    public boolean checkDevice(Interface interfaceCheck) {
        return deviceRepo.findById(interfaceCheck.getId())!=null?true:false;
    }

    @Override
    public boolean CheckInterfaceId(Long id) {
        return interfaceRepo.findInterfaceById(id)!=null?true:false;
    }

    @Override
    public boolean checkValidInterface(Interface interfaceCheck) {
        if (checkDevice(interfaceCheck)) {
            if (checkNetmask(interfaceCheck)) {
                if (checkIpAddress(interfaceCheck)) {
                    return true;
                }
                throw new DeviceException(Message.INVALID_DEVICE);
            }
            throw new DeviceException(Message.INVALID_IP);
        }
        throw new DeviceException(Message.INVALID_DEVICE);
    }

    @Override
    public void deleteById(Long id) {
        interfaceRepo.deleteById(id);
    }
}
