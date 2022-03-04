package com.example.managedevices.service.impl;

import com.example.managedevices.entity.Device;
import com.example.managedevices.repo.DeviceRepo;
import com.example.managedevices.service.DeviceService;
import com.example.managedevices.vadilation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {
    private final DeviceRepo deviceRepo;

    @Override
    public List<Device> getAllDevices() {
        return deviceRepo.findAll();
    }

    @Override
    public Device addDevice(Device device) {
        return (Device) deviceRepo.save(device);
    }

    @Override
    public boolean checkValidIpv4(Device device) {
        return EntityValidator.isValidIp(device.getIpAddress());
    }

    @Override
    public Device getDeviceById(Long id) {
        return deviceRepo.findDeviceById(id);
    }

    @Override
    public List<Device> getDevicesByType(String type) {
        return deviceRepo.findDeviceByType(type);
    }

    @Override
    public List<Device> searchDeviceByIpaddress(String ipAddress) {
        return deviceRepo.findDevicesByIpAddressContains(ipAddress);
    }

    @Override
    public void deleteDeviceById(Long id) {
        deviceRepo.deleteDeviceById(id);
    }

    @Override
    public boolean isValidId(Long id) {
        return deviceRepo.existsById(id);
    }


}
