package com.example.managedevices.mapper;

import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;

import java.util.Map;
import java.util.Objects;

public class DeviceMapper {
    public static Device mapConfigurationToDevice(Map<String,String> configuration, Device device){
        device.setName(configuration.get("Product name"));
        device.setFirmwareVersion(configuration.get("Firmware version"));
        device.setType(configuration.get("Firmware version").substring(0,3));
        device.setSerialNumber(configuration.get("Serial number"));
        return device;
    }

    public static Interface mapConfigurationToInterface(String[] configuration,Interface inf){
        inf.setName(configuration[0]);
        inf.setState(Objects.equals(configuration[1], "Enable"));
        inf.setDhcp(Objects.equals(configuration[2], "Enable"));
        inf.setIpAddress(Objects.equals(configuration[3], "---") ?"":configuration[6]);
        inf.setNetmask(Objects.equals(configuration[4], "---") ?"":configuration[6]);
        inf.setGateway(Objects.equals(configuration[5], "---") ?"":configuration[6]);
        inf.setInfo(Objects.equals(configuration[6], "---") ?"":configuration[6]);
        return inf;
    }

}
