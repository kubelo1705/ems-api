package com.example.managedevices.service.impl;

import com.example.managedevices.constant.BaseCommand;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.entity.Port;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.parser.OutputParser;
import com.example.managedevices.repository.*;
import com.example.managedevices.service.DeviceService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.OutputUtils;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepo;
    private final InterfaceRepository interfaceRepo;
    private final PortRepository portRepo;
    private final NtpServerRepository ntpRepo;
    private final NtpAddressRepository ntpAddressRepo;
    private static final Logger log = LogManager.getLogger(DeviceServiceImpl.class);

    @Override
    public List<Device> getAllDevices() {
        return deviceRepo.findAll();
    }

    @Override
    public Device addDevice(Device device) {
        if (isValidDevice(device))
            return deviceRepo.save(device);
        return null;
    }

    @Override
    public boolean isValidDevice(Device device) {
        if (device.getIpAddress() != null) {
            if(EntityValidator.isValidIp(device.getIpAddress())) {
                if(!deviceRepo.existsByIpAddress(device.getIpAddress())){
                    return true;
                }else {
                    throw new EmsException(Message.DUPLICATE_DEVICE);
                }
            }
        }
        throw new EmsException(Message.INVALID_DATA);
    }

    @Override
    public Device getDeviceById(Long id) {
        Device device = deviceRepo.findDeviceById(id);
        return device;
    }

    @Override
    public List<Device> getDevicesByType(String type) {
        List<Device> devices = deviceRepo.findDeviceByType(type);
        return devices;
    }

    @Override
    public Device getDeviceByIpaddress(String ipAddress) {
        Device device = deviceRepo.findDeviceByIpAddress(ipAddress);
        return device;
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
        interfaceRepo.deleteAllByDevice_Id(device.getId());
        portRepo.deleteAllByDevice_Id(device.getId());
        ntpRepo.deleteAllByDevice_Id(device.getId());
        ntpAddressRepo.deleteAllByNtpserver_Id(device.getNtpserver() == null ? 0L : device.getNtpserver().getId());
        try {
            String deviceConfigure = CommandUtils.execute(device, device.getCredential(), BaseCommand.DEVICE_CONFIGURE);
            if (!deviceConfigure.isBlank()) {
                device.setStatus(true);
                OutputParser.mapConfigurationToDevice(OutputUtils.toMapDeviceConfiguration(deviceConfigure), device);
                log.debug("MAP CONFIGURATIONS TO DEVICE:" + device.getIpAddress());

                String interfaceConfigurations = CommandUtils.execute(device, device.getCredential(), BaseCommand.INTERFACE_CONFIGURE);
                if (!interfaceConfigurations.isBlank()) {

                    Set<Interface> infs = new HashSet<>();
                    infs.addAll(OutputParser.mapConfigurationToInterfaces(interfaceConfigurations));
                    infs.forEach(inf -> {
                        inf.setDevice(device);
                        interfaceRepo.save(inf);
                    });
                    device.setInterfaces(infs);
                    log.debug("MAP INTERFACES TO DEVICE:" + device.getIpAddress());
                } else {
                    log.debug("EMPTY INTERFACES");
                }

                String portConfigurations = CommandUtils.execute(device, device.getCredential(), BaseCommand.PORT_CONFIGURE);
                if (!portConfigurations.isBlank()) {
                    Set<Port> ports = new HashSet<>();
                    ports.addAll(OutputParser.mapConfigurationToPorts(portConfigurations));
                    ports.forEach(port -> {
                        port.setDevice(device);
                        portRepo.save(port);
                    });
                    device.setPorts(ports);
                    log.debug("MAP PORTS TO DEVICE:" + device.getIpAddress());
                } else {
                    log.debug("EMPTY PORTS");
                }

                String ntpConfiguration = CommandUtils.execute(device, device.getCredential(), BaseCommand.NTP_CONFIGURE);
                if (!ntpConfiguration.isBlank()) {
                    Ntpserver ntpserver = OutputParser.mapConfigurationToNtp(ntpConfiguration);
                    ntpserver.setDevice(device);
                    ntpRepo.save(ntpserver);
                    ntpserver.getNtpaddresses().forEach(ntpaddress -> ntpAddressRepo.save(ntpaddress));
                    device.setNtpserver(ntpserver);
                    log.debug("MAP NTPS TO DEVICE:" + device.getIpAddress());
                } else {
                    log.debug("EMPTY NTPS");
                }

                return deviceRepo.save(device);
            }
        } catch (Exception e) {
            device.setStatus(false);
            deviceRepo.save(device);
            log.error(e.getMessage());
            log.error("CAN'T CREATE CONNECTION TO DEVICE:" + device.getIpAddress());
        }
        return device;
    }

    @Override
    public void autoResync() {
        deviceRepo.findAll().forEach(device -> {
            resync(device);
        });
    }

    @Override
    public String executeCommandByIdDevice(Long idDevice, String command) {
        Device device = deviceRepo.findDeviceById(idDevice);
        if (device == null) {
            throw new EmsException(Message.NON_EXIST_DEVICE);
        } else {
            String output = CommandUtils.execute(device, device.getCredential(), command);
            return OutputUtils.formatOutput(output);
        }
    }
}
