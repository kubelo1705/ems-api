package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.exception.DeviceException;
import com.example.managedevices.mapper.DeviceMapper;
import com.example.managedevices.repo.DeviceRepo;
import com.example.managedevices.repo.InterfaceRepo;
import com.example.managedevices.repo.PortRepo;
import com.example.managedevices.service.DeviceService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.FormatUtils;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepo deviceRepo;
    private final InterfaceRepo interfaceRepo;
    private final PortRepo portRepo;

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
            throw new DeviceException(Message.INVALID_IP);
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
            throw new DeviceException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public List<Device> getDevicesByType(String type) {
        List<Device> devices = deviceRepo.findDeviceByType(type);
        if (devices.isEmpty()) {
            throw new DeviceException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    @Override
    public List<Device> getDeviceByIpaddress(String ipAddress) {
        List<Device> devices = deviceRepo.findDevicesByIpAddressContains(ipAddress);
        if (devices.isEmpty()) {
            throw new DeviceException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    @Override
    public void deleteDeviceById(Long id) {
        if (isValidId(id)) {
            deviceRepo.deleteDeviceById(id);
        } else {
            throw new DeviceException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public boolean isValidId(Long id) {
        return deviceRepo.existsById(id);
    }

    @Override
    public void resync(Device device) {
        try {
            String deviceConfigure = CommandUtils.execute(device, device.getCredential(), Command.DEVICE_CONFIGURE);
            if (!deviceConfigure.isBlank()) {

                device.setStatus(true);
                DeviceMapper.mapConfigurationToDevice(FormatUtils.toMapDeviceConfiguration(deviceConfigure), device);
                log.debug("MAP CONFIGURATIONS TO DEVICE:"+device.getIpAddress());

                String interfaceConfigurations = CommandUtils.execute(device, device.getCredential(), Command.INTERFACE_CONFIGURE);
                if (!interfaceConfigurations.isBlank()) {
                    DeviceMapper.mapInterfacesToDevice(interfaceConfigurations, device);
                    log.debug("MAP INTERFACES TO DEVICE:"+device.getIpAddress());
                }else {
                    log.debug("EMPTY INTERFACES");
                }

                String portConfigurations = CommandUtils.execute(device, device.getCredential(), Command.PORT_CONFIGURE);
                if (!portConfigurations.isBlank()) {
                    DeviceMapper.mapPortsToDevice(portConfigurations, device);
                    log.debug("MAP PORTS TO DEVICE:"+device.getIpAddress());
                }else {
                    log.debug("EMPTY PORTS");
                }

                deviceRepo.save(device);
            }
        } catch (Exception e) {
            device.setStatus(false);
            deviceRepo.save(device);
            log.error("CAN'T CREATE CONNECTION TO DEVICE:"+device.getIpAddress());
        }
    }

    @Override
    public void autoResync() {
        deviceRepo.findAll().forEach(device -> {
            resync(device);
        });
    }
}
