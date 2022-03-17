package com.example.managedevices.service;

import com.example.managedevices.entity.Device;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

public interface DeviceService {
    List<Device> getAllDevices();
    Device addDevice(Device device);
    boolean checkValidIpv4(Device device);
    Device getDeviceById(Long id);
    List<Device> getDevicesByType(String type);
    List<Device> getDeviceByIpaddress(String ipAddress);
    void deleteDeviceById(Long id);
    boolean isValidId(Long id);
    Device resync(Device device);
    @Scheduled(fixedDelay = 15000) void autoResync();
    String executeCommandByIdDevice(Long idDevice, String command);

}
