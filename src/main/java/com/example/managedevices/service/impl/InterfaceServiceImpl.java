package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.InterfaceRepository;
import com.example.managedevices.response.EmsResponse;
import com.example.managedevices.service.InterfaceService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.OutputUtils;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public EmsResponse addInterface(Interface interfaceAdd) {
        if (interfaceRepo.existsById(interfaceAdd.getId())) {
            if (checkValidInterface(interfaceAdd)) {
                Device device=deviceRepo.findDeviceById(interfaceAdd.getDevice().getId());
                String command = Command.ADD_INTERFACE
                        .replace("interface_name",interfaceAdd.getName())
                        .replace("ip_address", interfaceAdd.getIpAddress())
                        .replace("port_name", interfaceAdd.getPort())
                        .replace("interface_state", interfaceAdd.isState()?"enable":"disable")
                        .replace("netmask_address", interfaceAdd.getNetmask());

                String output=OutputUtils.formatOutput(CommandUtils.execute(device,device.getCredential(),command));
                if (output.isEmpty())
                    return new EmsResponse(HttpStatus.OK,Message.SUCCESSFUL,interfaceRepo.save(interfaceAdd));
                else
                    return new EmsResponse(HttpStatus.BAD_REQUEST,Message.UNSUCCESSFUL,output);
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
        try {
            if(interfaceCheck.getDevice()!=null)
                return deviceRepo.existsById(interfaceCheck.getDevice().getId());
            else
                return false;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean checkValidInterface(Interface interfaceCheck) {
        if (checkDevice(interfaceCheck)) {
            if (checkNetmask(interfaceCheck)) {
                if (checkIpAddress(interfaceCheck)) {
                    return true;
                }
                throw new EmsException(Message.INVALID_NETMASK);
            }
            throw new EmsException(Message.INVALID_IP);
        }
        throw new EmsException(Message.NON_EXIST_DEVICE);
    }

    @Override
    public void deleteInterface(Long idDevice,String interfaceName) {
        if(deviceRepo.existsById(idDevice)) {
            if (interfaceRepo.existsByNameAndDevice_Id(interfaceName,idDevice)) {
                Device device=deviceRepo.findDeviceById(idDevice);
                String command=Command.DELETE_INTERFACE.replace("interface_name",interfaceName);
                String output = OutputUtils.formatOutput(CommandUtils.execute(device, device.getCredential(),command));
                if(!output.isEmpty()){
                    throw new EmsException(output);
                }else {
                    interfaceRepo.deleteByNameAndDevice_Id(interfaceName,idDevice);
                }
            } else {
                throw new EmsException(Message.NON_EXIST_INTERFACE);
            }
        }else {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public Interface updateInterface(Interface interfaceUpdate, Long id) {
        if (interfaceRepo.existsById(id)) {
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
