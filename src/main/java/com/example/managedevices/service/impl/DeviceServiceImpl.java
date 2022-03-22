package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.entity.Port;
import com.example.managedevices.exception.BadRequestException;
import com.example.managedevices.exception.ConflictException;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.exception.NotFoundException;
import com.example.managedevices.parser.CommandParser;
import com.example.managedevices.repository.*;
import com.example.managedevices.service.DeviceService;
import com.example.managedevices.utils.CommandUtils;
import com.example.managedevices.utils.ValidationUtils;
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
@Transactional
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
     * @return
     */
    @Override
    public List<Device> getAllDevices() {
        List<Device> devices=deviceRepo.findAll();
        if(devices.isEmpty()){
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    /**
     * add new devices to database
     * @param device
     * @return
     */
    @Override
    public Device addDevice(Device device) {
        if (credentialRepo.existsById(device.getCredential().getId())) {
            if (isValidDevice(device))
                return deviceRepo.save(device);
            throw new BadRequestException(Message.INVALID_DATA);
        } else {
            throw new NotFoundException(Message.NON_EXIST_CREDENTIAL);
        }
    }

    /**
     * check if this device is valid. check valid address and duplicate address or not
     * @param device
     * @return
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
     * @param id
     * @return
     */
    @Override
    public Device getDeviceById(Long id) {
        Device device = deviceRepo.findDeviceById(id);
        if(device==null){
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return device;
    }

    /**
     * get device by type in database
     * @param type
     * @return
     */
    @Override
    public List<Device> getDevicesByType(String type) {
        List<Device> devices = deviceRepo.findDeviceByType(type);
        if(devices.isEmpty()){
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
        return devices;
    }

    /**
     * get device by ip address in databse
     * @param ipAddress
     * @return
     */
    @Override
    public Device getDeviceByIpaddress(String ipAddress) {
        Device device = deviceRepo.findDeviceByIpAddress(ipAddress);
        if(device==null){
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
     * @param id
     * @return
     */
    @Override
    public boolean isValidId(Long id) {
        return deviceRepo.existsById(id);
    }

    /**
     * load all configuration from real device and map to device in database
     * @param device
     * @return
     */
    @Override
    public void resync(Device device) {
        interfaceRepo.deleteAllByDevice_Id(device.getId());
        portRepo.deleteAllByDevice_Id(device.getId());
        ntpRepo.deleteAllByDevice_Id(device.getId());
        ntpAddressRepo.deleteAllByNtpserver_Id(device.getNtpserver() == null ? 0L : device.getNtpserver().getId());
        try {
                Thread configurationDevice = new Thread(() -> {
                    if(!device.isConnected()) {
                        System.out.println("device");
                        getConfiguration(device);
                        System.out.println("done device");
                    }
                });

            Thread portsAnInterfaces=new Thread(()->{
                System.out.println("port and interfaces");
                Instant start=Instant.now();
                Set<Port> ports = getPorts(device);
                device.setPorts(ports);
                System.out.println("done ports:"+ Duration.between(Instant.now(),start));

                Set<Interface> infs = getInterfaces(device);
                device.setInterfaces(infs);
                System.out.println("done interfaces:"+ Duration.between(Instant.now(),start));
            });

            Thread ntp=new Thread(()->{
                System.out.println("ntp");
                Instant start=Instant.now();
                Ntpserver ntpserver = getNtp(device);
                device.setNtpserver(ntpserver);
                System.out.println("done ntp:"+ Duration.between(Instant.now(),start));
            });

            configurationDevice.start();
            portsAnInterfaces.start();
            ntp.start();

            ntp.join();
            configurationDevice.join();
            portsAnInterfaces.join();

            deviceRepo.save(device);
        } catch (Exception e) {
            device.setConnected(false);
            deviceRepo.save(device);
            log.error(e.getMessage());
            log.error("CAN'T CREATE CONNECTION TO DEVICE:" + device.getIpAddress());
        }
    }

    /**
     * auto resync device every 15s
     */
    @Override
    @Scheduled(fixedDelay = 15000)
    public void autoResync() {
        deviceRepo.findAll().forEach(device -> {
            resync(device);
        });
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
            String output = CommandUtils.execute(device, device.getCredential(), command);
            return CommandParser.formatOutput(output);
        }
    }

    /**
     * get interfaces from real device and map to list interface object
     * @param device
     * @return
     */
    public Set<Interface> getInterfaces(Device device) {
        String interfaceShowOutput = CommandUtils.execute(device, device.getCredential(), Command.INTERFACE_SHOW);
        Set<Interface> infs = new HashSet<>();
        if (!interfaceShowOutput.isBlank()) {

            infs.addAll(CommandParser.getAllInterfacesFromCommand(interfaceShowOutput));
            infs.forEach(inf -> {
                inf.setDevice(device);

                String interfaceDetails = CommandUtils.execute(device, device.getCredential(), Command.INTERFACE_SHOW + " " + inf.getName());
                String portName = CommandParser.getPortNameFromInterfaceDetails(interfaceDetails);
                Port port = portRepo.findPortByPortNameAndDevice_Id(portName.trim(), device.getId());
                inf.setPort(port);

                interfaceRepo.save(inf);
            });
            log.debug("MAP INTERFACES TO DEVICE:" + device.getIpAddress());
        } else {
            log.debug("EMPTY INTERFACES");
        }
        return infs;
    }

    /**
     * get ports from real device and map to list interface object
     * @param device
     * @return
     */
    public Set<Port> getPorts(Device device) {
        Set<Port> ports = new HashSet<>();
        String portConfigurations = CommandUtils.execute(device, device.getCredential(), Command.PORT_CONFIGURE);
        if (!portConfigurations.isBlank()) {
            ports.addAll(CommandParser.mapConfigurationToPorts(portConfigurations));
            ports.forEach(port -> {
                port.setDevice(device);
                portRepo.save(port);
            });
            device.setPorts(ports);
            log.debug("MAP PORTS TO DEVICE:" + device.getIpAddress());
        } else {
            log.debug("EMPTY PORTS");
        }
        return ports;
    }

    /**
     * get ntp server from real device and map to list ntp server object
     * @param device
     * @return
     */
    public Ntpserver getNtp(Device device) {
        Ntpserver ntpserver = new Ntpserver();
        String ntpConfiguration = CommandUtils.execute(device, device.getCredential(), Command.NTP_CONFIGURE);
        if (!ntpConfiguration.isBlank()) {
            ntpserver = CommandParser.mapConfigurationToNtp(ntpConfiguration);
            ntpserver.setDevice(device);
            ntpRepo.save(ntpserver);
            ntpserver.getNtpaddresses().forEach(ntpaddress -> ntpAddressRepo.save(ntpaddress));
            device.setNtpserver(ntpserver);
            log.debug("MAP NTPS TO DEVICE:" + device.getIpAddress());
        } else {
            log.debug("EMPTY NTPS");
        }
        return ntpserver;
    }

    /**
     * get basic configuration to device and map to device object
     * @param device
     */
    public void getConfiguration(Device device) {
        String deviceConfigure = CommandUtils.execute(device, device.getCredential(), Command.BOARD_SHOW_INFO);
        if (!deviceConfigure.isBlank()) {
            device.setConnected(true);
            CommandParser.mapConfigurationToDevice(CommandParser.toMapDeviceConfiguration(deviceConfigure), device);
        }
    }
}
