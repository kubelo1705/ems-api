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
import com.tma.ems.helper.CommandExecute;
import com.example.managedevices.parser.*;
import com.example.managedevices.repository.*;
import com.tma.ems.parser.*;
import com.tma.ems.service.DeviceService;
import com.tma.ems.utils.CommandUtils;
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

    /**
     * get all devices from database
     */
    @Override
    public List<Device> getAllDevices() {
        List<Device> devices = deviceRepo.findAll();
        if (devices.isEmpty()) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    /**
     * add new devices to database
     */
    @Override
    public Device addDevice(Device device) {
        if (credentialRepo.existsById(device.getCredential().getId())) {
            if (isValidDevice(device)) {
                Device deviceAdd = deviceRepo.save(device);
                deviceAdd.setCredential(credentialRepo.findCredentialById(device.getCredential().getId()));
//                getConfiguration(deviceAdd);
                new Thread(() -> {
//                    getPorts(deviceAdd);
//                    getInterfaces(deviceAdd);

                }).start();
                new Thread(() -> {
//                    getNtp(deviceAdd);
                }).start();
                return deviceAdd;
            }
            throw new BadRequestException(Message.INVALID_DATA);
        } else {
            throw new NotFoundException(Message.NON_EXIST_CREDENTIAL);
        }
    }

    /**
     * check if this device is valid. check valid address and duplicate address or not
     */
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

    /**
     * get device by id in database
     */
    @Override
    public Device getDeviceById(Long id) {
        Device device = deviceRepo.findDeviceById(id);
        if (device == null) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return device;
    }

    /**
     * get device by type in database
     *
     */
    @Override
    public List<Device> getDevicesByType(String type) {
        List<Device> devices = deviceRepo.findDeviceByType(type);
        if (devices.isEmpty()) {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    /**
     * get device by ip address in databse
     */
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

    /**
     * check if id device exist or not
     */
    @Override
    public boolean isValidId(Long id) {
        return deviceRepo.existsById(id);
    }

    /**
     * load all configuration from real device and map to device in database

     */
    @Transactional
    @Override
    public void resync(Device device) {
        interfaceRepo.deleteAllByDevice_Id(device.getId());
        portRepo.deleteAllByDevice_Id(device.getId());
        ntpRepo.deleteAllByDevice_Id(device.getId());
        ntpAddressRepo.deleteAllByNtpserver_Id(device.getNtpserver() == null ? 0L : device.getNtpserver().getId());

        CommandExecute commandExecute = new CommandExecute(device);

        try {
            if (!device.isConnected()) {
                getConfiguration(device, commandExecute);
            }

            System.out.println("ports and interfaces");
            getPorts(device, commandExecute);
            System.out.println("done ports");

            getInterfaces(device, commandExecute);

            System.out.println("ntp");
            getNtp(device, commandExecute);

            deviceRepo.save(device);
        } catch (Exception e) {
            device.setConnected(false);
            deviceRepo.save(device);
            log.error(e.getMessage());
        }finally {
            commandExecute.close();
        }
    }

    /**
     * auto resync device every 15s
     */
    @Override
    @Scheduled(fixedDelay = 15000)
    public void autoResync() {
        deviceRepo.findAll().forEach(this::resync);
    }

    /**
     * send a direct command to device and get output
     *
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
            String output = CommandUtils.execute(device, device.getCredential(), command);
            return CommandParser.formatOutput(output);
        }
    }

    /**
     * get interfaces from real device and map to list interface object
     */
    public void getInterfaces(Device device, CommandExecute commandExecute) {
        String interfaceShowOutput = commandExecute.executeCommand(Command.INTERFACE_SHOW);
        Set<Interface> infs = new HashSet<>();
        if (!interfaceShowOutput.isBlank()) {

            infs.addAll(InterfaceParser.convertOutputCommandToInterfaces(interfaceShowOutput));
            infs.forEach(inf -> {
                inf.setDevice(device);
                Instant start = Instant.now();
                String interfaceDetails = commandExecute.executeCommand(Command.INTERFACE_SHOW + " " + inf.getName());
                System.out.println("Interface:" + Duration.between(Instant.now(), start));
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
     * get ports from real device and map to list interface object
     */
    public void getPorts(Device device, CommandExecute commandExecute) {
        Set<Port> ports = new HashSet<>();
        String portConfigurations = commandExecute.executeCommand(Command.PORT_SHOW_CONFIGURATION);
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
     * get ntp server from real device and map to list ntp server object
     *
     * @param device
     * @return
     */
    public void getNtp(Device device, CommandExecute commandExecute) {
        String ntpConfiguration = commandExecute.executeCommand(Command.NTP_SHOW);
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
    public void getConfiguration(Device device, CommandExecute commandExecute) {
        String deviceConfiguration = commandExecute.executeCommand(Command.BOARD_SHOW_INFO);
        System.out.println(deviceConfiguration);
        if (!deviceConfiguration.isBlank()) {
            device.setConnected(true);
            DeviceParser.mapOutputCommandToDevice(deviceConfiguration,device);
        }
        deviceRepo.save(device);
        System.out.println("Done device");
    }
}
