package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.entity.Port;
import com.example.managedevices.exception.BadRequestException;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.exception.NotFoundException;
import com.example.managedevices.mapper.ObjectMapper;
import com.example.managedevices.parser.CommandParser;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.InterfaceRepository;
import com.example.managedevices.repository.PortRepository;
import com.example.managedevices.service.InterfaceService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * solve logic related to interface
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InterfaceServiceImpl implements InterfaceService {
    private final InterfaceRepository interfaceRepo;
    private final DeviceRepository deviceRepo;
    private final PortRepository portRepo;

    @Override
    public List<Interface> getInterfacesByDeviceId(Long idDevice) {
        if(deviceRepo.existsById(idDevice)) {
            if(deviceRepo.existsByCredential_IdAndConnected(idDevice,true)) {
                List<Interface> interfaces = interfaceRepo.findInterfaceByDevice_Id(idDevice);
                if (interfaces.isEmpty()) {
                    throw new NotFoundException(Message.NON_EXIST_INTERFACE + " WITH DEVICE ID=" + idDevice);
                }
                return interfaces;
            }else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        }else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public Interface addInterface(Long idDevice, Map<String, Object> map) {
        if (deviceRepo.existsById(idDevice)) {
            Device device = deviceRepo.findDeviceById(idDevice);
            String command = CommandUtils.toInterfaceCommand(Command.ADD_INTERFACE, map);
            if(device.isConnected()) {
                String output = CommandUtils.execute(device, device.getCredential(), command);

                if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                    throw new BadRequestException(CommandParser.formatOutput(output));
                } else {
                    Interface inf = ObjectMapper.mapToInterface(map);

                    Port port = portRepo.findPortByPortNameAndDevice_Id(map.get("port_name").toString(), device.getId());
                    inf.setPort(port);
                    inf.setDevice(device);
                    return interfaceRepo.save(inf);
                }
            }else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
//            if (ValidationUtils.isValidIp(interfaceAdd.getIpAddress())) {
//                Device device = deviceRepo.findDeviceById(interfaceAdd.getDevice().getId());
//                String command = CommandUtils.toInterfaceCommand(BaseCommand.ADD_INTERFACE, interfaceAdd);
//                System.out.println(command);
//                String output = (CommandUtils.execute(device, device.getCredential(), command));
//                if (!OutputUtils.isErrorOutput(device.getSerialNumber(), command, output)) {
//                    return interfaceAdd;
//                } else {
//                    throw new EmsException(OutputUtils.formatOutput(output));
//                }
//            } else {
//                throw new EmsException(Message.INVALID_DATA);
//            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public void deleteInterface(Long idDevice, String interfaceName) {
        if (deviceRepo.existsById(idDevice)) {
            Device device = deviceRepo.findDeviceById(idDevice);
            if(device.isConnected()) {
                if (interfaceRepo.existsByNameAndDevice_Id(interfaceName, idDevice)) {
                    String command = Command.DELETE_INTERFACE.replace("interface_name", interfaceName);
                    String output = (CommandUtils.execute(device, device.getCredential(), command));
                    if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                        output = CommandParser.formatOutput(output);
                        throw new BadRequestException(output);
                    } else {
                        interfaceRepo.deleteByNameAndDevice_Id(interfaceName, idDevice);
                    }
                } else {
                    throw new NotFoundException(Message.NON_EXIST_INTERFACE);
                }
            }else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public Interface updateInterface(Long idDevice, Map<String, Object> map) {
        if (deviceRepo.existsById(idDevice)) {
            Device device = deviceRepo.findDeviceById(idDevice);
            if(device.isConnected()) {
                if (interfaceRepo.existsByNameAndDevice_Id(map.get("interface_name").toString(), idDevice)) {
                    String command = CommandUtils.toInterfaceCommand(Command.EDIT_INTERFACE, map);
                    System.out.println(command);
                    String output = CommandUtils.execute(device, device.getCredential(), command);
                    if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                        throw new BadRequestException(CommandParser.formatOutput(output));
                    } else {
                        Interface inf = interfaceRepo.findInterfaceByNameAndDevice_Id(map.get("interface_name").toString(), idDevice);
                        if (map.get("new_name") != null) {
                            inf.setName(map.get("new_name").toString());
                        }
                        if (map.get("port_name") != null) {
                            Port port = portRepo.findPortByPortNameAndDevice_Id(map.get("port_name").toString(), idDevice);
                            inf.setPort(port);
                        }
                        if (map.get("ip_address") != null) {
                            inf.setIpAddress(map.get("ip_address").toString());
                        }
                        if (map.get("netmask") != null) {
                            inf.setNetmask(map.get("netmask").toString());
                        }
                        if (map.get("interface_state") != null) {
                            inf.setState(map.get("interface_state").toString().equalsIgnoreCase("enable"));
                        }
                        return interfaceRepo.save(inf);
                    }
                } else {
                    throw new NotFoundException(Message.NON_EXIST_INTERFACE);
                }
            }else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }
}
