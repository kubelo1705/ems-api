package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.entity.Port;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.parser.OutputParser;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.repository.InterfaceRepository;
import com.example.managedevices.repository.NtpServerRepository;
import com.example.managedevices.repository.PortRepository;
import com.example.managedevices.service.DeviceService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.OutputUtils;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.managedevices.parser.OutputParser.mapConfigurationToInterface;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepo;
    private final InterfaceRepository interfaceRepo;
    private final PortRepository portRepo;
    private final NtpServerRepository ntpRepo;
    private static final Logger log= LogManager.getLogger(DeviceServiceImpl.class);

    @Override
    public List<Device> getAllDevices() {
        return deviceRepo.findAll();
    }

    @Override
    public Device addDevice(Device device) {
        if(checkValidIpv4(device))
            return deviceRepo.save(device);
        else
            throw new EmsException(Message.INVALID_IP);
    }

    @Override
    public boolean checkValidIpv4(Device device) {
        return EntityValidator.isValidIp(device.getIpAddress());
    }

    @Override
    public Device getDeviceById(Long id) {
        Device device = deviceRepo.findDeviceById(id);
        if (device != null) {
            return device;
        } else {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public List<Device> getDevicesByType(String type) {
        List<Device> devices = deviceRepo.findDeviceByType(type);
        if (devices.isEmpty()) {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    @Override
    public List<Device> getDeviceByIpaddress(String ipAddress) {
        List<Device> devices = deviceRepo.findDevicesByIpAddressContains(ipAddress);
        if (devices.isEmpty()) {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    @Override
    public void deleteDeviceById(Long id) {
        if (isValidId(id)) {
            deviceRepo.deleteDeviceById(id);
        } else {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public boolean isValidId(Long id) {
        return deviceRepo.existsById(id);
    }

    @Override
    public Device resync(Device device) {
        interfaceRepo.deleteAll();
        portRepo.deleteAll();
        ntpRepo.deleteAll();
        try {
            String deviceConfigure = CommandUtils.execute(device, device.getCredential(), Command.DEVICE_CONFIGURE);
            if (!deviceConfigure.isBlank()) {
                device.setStatus(true);
                OutputParser.mapConfigurationToDevice(OutputUtils.toMapDeviceConfiguration(deviceConfigure), device);
                log.debug("MAP CONFIGURATIONS TO DEVICE:"+device.getIpAddress());

                String interfaceConfigurations = CommandUtils.execute(device, device.getCredential(), Command.INTERFACE_CONFIGURE);
                if (!interfaceConfigurations.isBlank()) {

                    Set<Interface> infs=new HashSet<>();
                    infs.addAll(OutputParser.mapConfigurationToInterfaces(interfaceConfigurations));
                    infs.forEach(inf->{
                        inf.setDevice(device);
                        System.out.println(inf.getName());
                        interfaceRepo.save(inf);
                    });
//                    device.setInterfaces(interfaces);
                    log.debug("MAP INTERFACES TO DEVICE:"+device.getIpAddress());
                }else {
                    log.debug("EMPTY INTERFACES");
                }

                String portConfigurations = CommandUtils.execute(device, device.getCredential(), Command.PORT_CONFIGURE);
                if (!portConfigurations.isBlank()) {
                    Set<Port> ports=new HashSet<>();
                    ports.addAll(OutputParser.mapConfigurationToPorts(portConfigurations));
                    ports.forEach(port->{
                        port.setDevice(device);
                        System.out.println(port.getPortName());
                        portRepo.save(port);
                    });
                    log.debug("MAP PORTS TO DEVICE:"+device.getIpAddress());
                }else {
                    log.debug("EMPTY PORTS");
                }

                String ntpConfiguration=CommandUtils.execute(device,device.getCredential(),Command.NTP_CONFIGURE);
                if(!ntpConfiguration.isBlank()){

                }

                return deviceRepo.save(device);
            }
        } catch (Exception e) {
            device.setStatus(false);
            deviceRepo.save(device);
            log.error(e.getMessage());
            log.error("CAN'T CREATE CONNECTION TO DEVICE:"+device.getIpAddress());
        }
        return device;
    }

    @Override
    public void autoResync() {
        deviceRepo.findAll().forEach(device -> {
            resync(device);
        });
    }
}
