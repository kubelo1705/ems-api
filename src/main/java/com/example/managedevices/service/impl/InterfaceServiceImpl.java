package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.InterfaceRepository;
import com.example.managedevices.service.InterfaceService;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InterfaceServiceImpl implements InterfaceService {
    private final InterfaceRepository interfaceRepo;
    private final DeviceRepository deviceRepo;

    @Override
    public List<Interface> getAllInterfaces() {
        return interfaceRepo.findAll();
    }

    @Override
    public List<Interface> getInterfacesByDeviceId(Long id) {
        return interfaceRepo.findInterfaceByDevice_Id(id);
    }

    @Override
    public Interface addInterface(Interface interfaceAdd) {
        if (!CheckInterfaceId(interfaceAdd.getId())) {
            if (checkValidInterface(interfaceAdd)) {
                return interfaceRepo.save(interfaceAdd);
            } else {
                throw new EmsException(Message.INVALID_DATA);
            }
        }
        throw new EmsException(Message.DUPLICATE_ID);
    }

    @Override
    public boolean checkNetmask(Interface interfaceCheck) {
        return EntityValidator.isValidNetmask(interfaceCheck.getNetmask());
    }

    @Override
    public boolean checkIpAddress(Interface interfaceCheck) {
        return EntityValidator.isValidIp(interfaceCheck.getIpAddress());
    }

    @Override
    public boolean checkDevice(Interface interfaceCheck) {
        return deviceRepo.findById(interfaceCheck.getId()) != null ? true : false;
    }

    @Override
    public boolean CheckInterfaceId(Long id) {
        return interfaceRepo.findInterfaceById(id) != null ? true : false;
    }

    @Override
    public boolean checkValidInterface(Interface interfaceCheck) {
        if (checkDevice(interfaceCheck)) {
            if (checkNetmask(interfaceCheck)) {
                if (checkIpAddress(interfaceCheck)) {
                    return true;
                }
                throw new EmsException(Message.INVALID_DEVICE);
            }
            throw new EmsException(Message.INVALID_IP);
        }
        throw new EmsException(Message.INVALID_DEVICE);
    }

    @Override
    public void deleteInterfaceById(Long id) {
        if (CheckInterfaceId(id)) {
            interfaceRepo.deleteById(id);
        } else {
            throw new EmsException(Message.NON_EXIST_INTERFACE);
        }
    }

    @Override
    public Interface updateInterface(Interface interfaceUpdate, Long id) {
        if (CheckInterfaceId(id)) {
            if (checkValidInterface(interfaceUpdate)) {
                Interface inf = interfaceRepo.findInterfaceById(id);

                inf.setName(interfaceUpdate.getName());
                inf.setInfo((interfaceUpdate.getInfo()));
                inf.setGateway(interfaceUpdate.getGateway());
                inf.setNetmask(interfaceUpdate.getNetmask());
                inf.setDhcp(interfaceUpdate.isDhcp());
                inf.setState(interfaceUpdate.isState());
                inf.setIpAddress(interfaceUpdate.getIpAddress());

                return interfaceRepo.save(inf);
            } else {
                throw new EmsException(Message.INVALID_DATA);
            }
        } else {
            throw new EmsException(Message.NON_EXIST_INTERFACE);
        }
    }
}
