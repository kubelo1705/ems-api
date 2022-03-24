package com.tma.ems.service.impl;

import com.tma.ems.constant.Command;
import com.tma.ems.constant.Message;
import com.tma.ems.entity.Device;
import com.tma.ems.entity.Interface;
import com.tma.ems.entity.Ntpserver;
import com.tma.ems.entity.Port;
import com.tma.ems.exception.BadRequestException;
import com.tma.ems.exception.ConflictException;
import com.tma.ems.exception.NotFoundException;
import com.tma.ems.parser.*;
import com.tma.ems.service.DeviceService;
import com.tma.ems.utils.SshUtils;
import com.tma.ems.utils.ValidationUtils;
import com.tma.ems.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * solver logic related to device
 */
@Service
@RequiredArgsConstructor
//@EnableScheduling
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepository deviceRepo;
    private final InterfaceRepository interfaceRepo;
    private final PortRepository portRepo;
    private final NtpServerRepository ntpRepo;
    private final NtpAddressRepository ntpAddressRepo;
    private final CredentialRepository credentialRepo;
    private static final Logger log = LogManager.getLogger(DeviceServiceImpl.class);

    @Override
    public List<Device> getAllDevices() {
        List<Device> devices = deviceRepo.findAll();
        if (devices.isEmpty()) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    @Override
    public Device addDevice(Device device) {
        if (credentialRepo.existsById(device.getCredential().getId())) {
            if (isValidDevice(device)) {
                Device deviceAdd = deviceRepo.save(device);
                deviceAdd.setCredential(credentialRepo.findCredentialById(device.getCredential().getId()));
                //load configuration of device after save
                getConfiguration(deviceAdd);
                new Thread(() -> {
                    getPorts(deviceAdd);
                    getInterfaces(deviceAdd);
                }).start();
                new Thread(() -> {
                    getNtp(deviceAdd);
                }).start();
                return deviceAdd;
            }
            throw new BadRequestException(Message.INVALID_DATA);
        } else {
            throw new NotFoundException(Message.NON_EXIST_CREDENTIAL);
        }
    }

    @Override
    public boolean isValidDevice(Device device) {
        if (device.getIpAddress() != null) {
            if (ValidationUtils.isValidIp(device.getIpAddress())) {
                if (!deviceRepo.existsByIpAddress(device.getIpAddress())) {
                    return true;
                } else {
                    throw new ConflictException(Message.DUPLICATE_DEVICE);
                }
            }
        }
        throw new BadRequestException(Message.INVALID_DATA);
    }

    @Override
    public Device getDeviceById(Long id) {
        Device device = deviceRepo.findDeviceById(id);
        if (device == null) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return device;
    }

    @Override
    public List<Device> getDevicesByType(String type) {
        List<Device> devices = deviceRepo.findDeviceByType(type);
        if (devices.isEmpty()) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    @Override
    public Device getDeviceByIpaddress(String ipAddress) {
        Device device = deviceRepo.findDeviceByIpAddress(ipAddress);
        if (device == null) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return device;
    }

    @Override
    public void deleteDeviceById(Long id) {
        if (isValidId(id)) {
            deviceRepo.deleteDeviceById(id);
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }

    @Override
    public boolean isValidId(Long id) {
        return deviceRepo.existsById(id);
    }

    @Override
    public void cleanUpData(Device device) {
        interfaceRepo.deleteAllByDevice_Id(device.getId());
        portRepo.deleteAllByDevice_Id(device.getId());
        ntpRepo.deleteAllByDevice_Id(device.getId());
        ntpAddressRepo.deleteAllByNtpserver_Id(device.getNtpserver() == null ? 0L : device.getNtpserver().getId());
    }

    @Transactional
    @Override
    public void resync(Device device) {
        cleanUpData(device);
        try {
            //get device configuration and save to device if it isnot connected
            if (!device.isConnected()) {
                System.out.println("device");
                device.setConnected(true);
                getConfiguration(device);
                System.out.println("done device");
            }
            //get ports and save to device
            System.out.println("ports and interfaces");
            getPorts(device);
            System.out.println("done ports");
            //get interface and save to device
            getInterfaces(device);
            System.out.println("done interfaces");
            //get ntp server and save to device
            System.out.println("ntp");
            getNtp(device);
            System.out.println("done ntp");

            deviceRepo.save(device);
        } catch (Exception e) {
            device.setConnected(false);
            deviceRepo.save(device);
            log.error(e.getMessage());
        }
    }

    @Override
    @Scheduled(fixedDelay = 15000)
    public void autoResync() {
        deviceRepo.findAll().forEach(this::resync);
    }

    /**
     * send a direct command to device and get output
     * @param idDevice
     * @param command
     * @return
     */
    @Override
    public String executeCommandByIdDevice(Long idDevice, String command) {
        Device device = deviceRepo.findDeviceById(idDevice);
        if (device == null) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        } else {
            String output = SshUtils.executeCommand(device, command);
            return CommandParser.formatOutput(output);
        }
    }

    /**
     * get interfaces from device and map to list interface object
     */
    public void getInterfaces(Device device) {
        String interfaceShowOutput = SshUtils.executeCommand(device,Command.INTERFACE_SHOW);
        Set<Interface> infs = new HashSet<>();
        if (!interfaceShowOutput.isBlank()) {

            infs.addAll(InterfaceParser.convertOutputCommandToInterfaces(interfaceShowOutput));
            infs.forEach(inf -> {
                inf.setDevice(device);
                Instant start = Instant.now();
                //create command and execute to get interface details
                String interfaceDetails = SshUtils.executeCommand(device,Command.INTERFACE_SHOW + " " + inf.getName());
                System.out.println("Interface:" + Duration.between(Instant.now(), start));
                //get port name from interface details and save to interface
                String portName = InterfaceParser.getPortNameFromOutputCommand(interfaceDetails);
                Port port = portRepo.findPortByPortNameAndDevice_Id(portName.trim(), device.getId());
                inf.setPort(port);
                interfaceRepo.save(inf);
            });
            device.setInterfaces(infs);
            log.debug("MAP INTERFACES TO DEVICE:" + device.getIpAddress());
        } else {
            log.debug("EMPTY INTERFACES");
        }
    }

    /**
     * get ports from device and map to list interface object
     */
    public void getPorts(Device device) {
        Set<Port> ports = new HashSet<>();
        String portConfigurations = SshUtils.executeCommand(device,Command.PORT_SHOW_CONFIGURATION);
        if (!portConfigurations.isBlank()) {
            ports.addAll(PortParser.convertOutputCommandToPorts(portConfigurations));
            ports.forEach(port -> {
                port.setDevice(device);
                portRepo.save(port);
            });
            device.setPorts(ports);
            log.debug("MAP PORTS TO DEVICE:" + device.getIpAddress());
        } else {
            log.debug("EMPTY PORTS");
        }
    }

    /**
     * get ntp server from device and map to list ntp server object
     * @param device
     * @return
     */
    public void getNtp(Device device) {
        String ntpConfiguration = SshUtils.executeCommand(device,Command.NTP_SHOW);
        if (!ntpConfiguration.isBlank()) {
            Ntpserver ntpserver = NtpParser.convertOutputCommandToNtp(ntpConfiguration);
            ntpserver.setDevice(device);
            ntpRepo.save(ntpserver);
            ntpserver.getNtpaddresses().forEach(ntpaddress -> ntpAddressRepo.save(ntpaddress));
            device.setNtpserver(ntpserver);
            log.debug("MAP NTPS TO DEVICE:" + device.getIpAddress());
        } else {
            log.debug("EMPTY NTPS");
        }
    }

    /**
     * get basic configuration to device and map to device object
     */
    public void getConfiguration(Device device) {
        String deviceConfiguration = SshUtils.executeCommand(device,Command.BOARD_SHOW_INFO);
        if (!deviceConfiguration.isBlank()) {
            DeviceParser.mapOutputCommandToDevice(deviceConfiguration,device);
        }
        deviceRepo.save(device);
    }
}
