package com.example.managedevices.parser;

import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.entity.Port;
import com.example.managedevices.utils.OutputUtils;

import java.util.Map;
import java.util.Objects;

public class OutputParser {
    public static void mapConfigurationToDevice(Map<String,String> configuration, Device device){
        device.setName(configuration.get("Product name"));
        device.setFirmwareVersion(configuration.get("Firmware version"));
        device.setType(configuration.get("Firmware version").substring(0,3));
        device.setSerialNumber(configuration.get("Serial number"));
    }

    public static void mapConfigurationToInterface(String[] configurations,Interface inf){
        inf.setName(configurations[0]);
        inf.setState(Objects.equals(configurations[1], "Enable"));
        inf.setDhcp(Objects.equals(configurations[2], "Enable"));
        inf.setIpAddress(Objects.equals(configurations[3], "---") ?"":configurations[3]);
        inf.setNetmask(Objects.equals(configurations[4], "---") ?"":configurations[4]);
        inf.setGateway(Objects.equals(configurations[5], "---") ?"":configurations[5]);
        inf.setInfo(Objects.equals(configurations[6], "---") ?"":configurations[6]);
    }

    public static void mapConfigurationToPort(String[] configurations, Port port){
        port.setConnector(configurations[0].equals("---")?"":configurations[0]);
        port.setPortName(configurations[1]);
        port.setState(configurations[2].equals("Enable"));
        port.setSpeed(configurations[3]);
        port.setMtu(configurations[4].equals("---")?"":configurations[4]);
        port.setMdi(configurations[5]);
        port.setMacAddress(configurations[6]);
    }

    public static void mapStatusToPort(String status, Port port){
        port.setStatus(status.equals("Enable"));
    }

    public static void mapInterfacesToDevice(String interfaces,Device device){
        String[] configurations= OutputUtils.toArrayConfigurations(interfaces);
        for (String configuration : configurations) {
            Interface inf=new Interface();
            String[] properties= OutputUtils.toArrayProperties(configuration);
            mapConfigurationToInterface(properties,inf);
            device.getInterfaces().add(inf);
        }
    }
    public static void mapPortsToDevice(String ports,Device device){
        String[] configurations= OutputUtils.toArrayConfigurations(ports);
        for (String configuration : configurations) {
            Port port=new Port();
            String[] properties= OutputUtils.toArrayProperties(configuration);
            mapConfigurationToPort(properties,port);
            device.getPorts().add(port);
        }
    }
}
