package com.example.managedevices.service;

import com.example.managedevices.entity.Device;

import java.util.List;

public interface DeviceService {
    List<Device> getAllDevices();
    Device addDevice(Device device);
    boolean checkValidIpv4(Device device);
    Device getDeviceById(Long id);
    List<Device> getDevicesByType(String type);
    List<Device> searchDeviceByIpaddress(String ipAddress);
    void deleteDeviceById(Long id);
    boolean isValidId(Long id);
}
