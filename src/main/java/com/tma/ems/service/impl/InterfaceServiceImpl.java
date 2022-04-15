package com.tma.ems.service.impl;

import com.tma.ems.constant.Command;
import com.tma.ems.constant.Message;
import com.tma.ems.entity.Device;
import com.tma.ems.entity.Interface;
import com.tma.ems.entity.Port;
import com.tma.ems.exception.BadRequestException;
import com.tma.ems.exception.NotFoundException;
import com.tma.ems.parser.CommandParser;
import com.tma.ems.parser.InterfaceParser;
import com.tma.ems.repository.DeviceRepository;
import com.tma.ems.repository.InterfaceRepository;
import com.tma.ems.repository.PortRepository;
import com.tma.ems.service.InterfaceService;
import com.tma.ems.utils.SshUtils;
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
        if (deviceRepo.existsById(idDevice)) {
            if (deviceRepo.existsByIdAndConnected(idDevice, true)) {
                List<Interface> interfaces = interfaceRepo.findInterfaceByDevice_Id(idDevice);
                if (interfaces.isEmpty()) {
                    throw new NotFoundException(Message.NON_EXIST_INTERFACE + " WITH DEVICE ID=" + idDevice);
                }
                return interfaces;
            } else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public Interface addInterface(Long idDevice, Map<String, Object> map) {
        if (deviceRepo.existsById(idDevice)) {
            Device device = deviceRepo.findDeviceById(idDevice);
            String command = InterfaceParser.parseMapToCommand(Command.INTERFACE_ADD, map);
            if (device.isConnected()) {
                String output = SshUtils.executeCommand(device, command);
                System.out.println(output);
                if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                    throw new BadRequestException(CommandParser.formatOutput(output));
                } else {
                    Port port = portRepo.findPortByPortNameAndDevice_Id(map.get("port_name").toString(), device.getId());
                    Interface inf = create(map, port, device);
                    return interfaceRepo.save(inf);
                }
            } else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public void deleteInterface(Long idDevice, String interfaceName) {
        if (deviceRepo.existsById(idDevice)) {
            Device device = deviceRepo.findDeviceById(idDevice);
            if (device.isConnected()) {
                if (interfaceRepo.existsByNameAndDevice_Id(interfaceName, idDevice)) {
                    String command = Command.INTERFACE_DELETE.replace("interface_name", interfaceName);
                    String output = (SshUtils.executeCommand(device, command));
                    if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                        output = CommandParser.formatOutput(output);
                        throw new BadRequestException(output);
                    } else {
                        interfaceRepo.deleteByNameAndDevice_Id(interfaceName, idDevice);
                    }
                } else {
                    throw new NotFoundException(Message.NON_EXIST_INTERFACE);
                }
            } else {
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

            if (device.isConnected()) {
                if (interfaceRepo.existsByNameAndDevice_Id(map.get("interface_name").toString(), idDevice)) {

                    String command = InterfaceParser.parseMapToCommand(Command.INTERFACE_EDIT, map);
                    String output = SshUtils.executeCommand(device, command);
                    System.out.println(output);

                    if (CommandParser.isErrorOutput(device.getSerialNumber(), command, output)) {
                        throw new BadRequestException(CommandParser.formatOutput(output));
                    } else {
                        Interface inf = interfaceRepo.findInterfaceByNameAndDevice_Id(map.get("interface_name").toString(), idDevice);
                        if (map.get("port_name") != null) {
                            Port port = portRepo.findPortByPortNameAndDevice_Id(map.get("port_name").toString(), idDevice);
                            inf.setPort(port);
                        }

                        InterfaceParser.parseMapToInterface(map, inf);

                        return interfaceRepo.save(inf);
                    }
                } else {
                    throw new NotFoundException(Message.NON_EXIST_INTERFACE);
                }
            } else {
                throw new BadRequestException(Message.HAVENT_CREATED_CONNECTION);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public Interface create(Map<String, Object> map, Port port, Device device) {
        Interface inf = new Interface();
        InterfaceParser.parseMapToInterface(map, inf);
        inf.setPort(port);
        inf.setDevice(device);
        return inf;
    }
}
