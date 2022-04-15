package com.tma.ems.service;

import com.tma.ems.entity.Device;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface DeviceService {
    /**
     * get all devices from database
     *
     * @return
     */
    List<Device> getAllDevices();

    /**
     * add new device to database
     *
     * @param device
     * @return
     */
    Device addDevice(Device device);

    /**
     * check if device is valid
     *
     * @param device
     * @return
     */
    boolean isValidDevice(Device device);

    /**
     * get device from database by id
     *
     * @param id
     * @return
     */
    Device getDeviceById(Long id);

    /**
     * get device from database by type
     *
     * @param type
     * @return
     */
    List<Device> getDevicesByType(String type);

    /**
     * get device in database by ipaddress
     *
     * @param ipAddress
     * @return
     */
    Device getDeviceByIpaddress(String ipAddress);

    /**
     * delete device in data base by id
     *
     * @param id
     */
    void deleteDeviceById(Long id);

    /**
     * check if id is duplicate or not
     *
     * @param id
     * @return
     */
    boolean isValidId(Long id);

    /**
     * reload all configuration of device
     *
     * @param device
     */
    void reload(Device device);

    /**
     * auto reload configuration of device every 15s
     */
    @Scheduled(fixedDelay = 15000)
    void autoReload();

    /**
     * send a command directly to a managed device
     *
     * @param idDevice
     * @param command
     * @return
     */
    String executeCommandByIdDevice(Long idDevice, String command);

    /**
     * clean up configuration data before reload device
     *
     * @param device
     */
    void cleanUpData(Device device);

}
