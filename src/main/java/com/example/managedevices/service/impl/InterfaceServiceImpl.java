package com.example.managedevices.service.impl;

import com.example.managedevices.constant.BaseCommand;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.InterfaceRepository;
import com.example.managedevices.service.InterfaceService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.OutputUtils;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
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
    public Interface addInterface(Interface interfaceAdd,Long idDevice) {
        if (interfaceRepo.existsById(interfaceAdd.getId())) {
            if(deviceRepo.existsById(idDevice)) {
                if (EntityValidator.isValidIp(interfaceAdd.getIpAddress())) {
                    Device device = deviceRepo.findDeviceById(interfaceAdd.getDevice().getId());
                    String command = CommandUtils.toInterfaceCommand(BaseCommand.ADD_INTERFACE,interfaceAdd);

                    String output = (CommandUtils.execute(device, device.getCredential(), command));
                    if(!OutputUtils.isErrorOutput(device.getSerialNumber(),command,output)) {
                            return interfaceAdd;
                    }else {
                        throw new EmsException(OutputUtils.formatOutput(output));
                    }
                } else {
                    throw new EmsException(Message.INVALID_DATA);
                }
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
    public boolean checkValidInterface(Interface interfaceCheck) {
            if (checkNetmask(interfaceCheck)) {
                if (checkIpAddress(interfaceCheck)) {
                    return true;
                }
                throw new EmsException(Message.INVALID_NETMASK);
            }
            throw new EmsException(Message.INVALID_IP);
    }

    @Override
    public void deleteInterface(Long idDevice,String interfaceName) {
        if(deviceRepo.existsById(idDevice)) {
            if (interfaceRepo.existsByNameAndDevice_Id(interfaceName,idDevice)) {
                Device device=deviceRepo.findDeviceById(idDevice);
                String command= BaseCommand.DELETE_INTERFACE.replace("interface_name",interfaceName);
                String output = (CommandUtils.execute(device, device.getCredential(),command));
                if(OutputUtils.isErrorOutput(device.getSerialNumber(),command,output)){
                    output=OutputUtils.formatOutput(output);
                    throw new EmsException(output);
                }
                else{
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
    public Interface updateInterface(String interfaceName,Interface interfaceUpdate, Long idDevice) {
        if(deviceRepo.existsById(idDevice)){
            if(interfaceUpdate.getName()!=null){
                if(interfaceRepo.existsByNameAndDevice_Id(interfaceUpdate.getName(), idDevice)){
                    String basicCommand=BaseCommand.EDIT_INTERFACE;
                    Interface inf=interfaceRepo.findByName(interfaceUpdate.getName());
                    Device device=deviceRepo.findDeviceById(idDevice);

                    if(interfaceUpdate.getName()!=interfaceName){
                        basicCommand=basicCommand.replace("interface_name",inf.getName());
                        inf.setName(interfaceUpdate.getName());
                    }
                    if(interfaceUpdate.getNetmask()!=null){
                        inf.setNetmask(interfaceUpdate.getNetmask());
                    }
                    if(interfaceUpdate.getPort()!=null){
                        inf.setPort(interfaceUpdate.getPort());
                    }
                    if(interfaceUpdate.getIpAddress()!=null){
                        inf.setIpAddress(inf.getIpAddress());
                    }
                    if(interfaceUpdate.isState()!=false){
                        inf.setState(true);
                    }

                    String command=CommandUtils.toInterfaceCommand(basicCommand,inf);
                    String output=CommandUtils.execute(device,device.getCredential(),command);
                    if(OutputUtils.isErrorOutput(device.getSerialNumber(),command,output)){
                        throw new EmsException(OutputUtils.formatOutput(output));
                    }else {
                        return interfaceRepo.save(inf);
                    }
                }else {
                    throw new EmsException(Message.NON_EXIST_INTERFACE);
                }
            }else{
                throw new EmsException(Message.INVALID_DATA);
            }
        }else {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        }
    }
}
