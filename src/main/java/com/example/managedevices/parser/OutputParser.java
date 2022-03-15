package com.example.managedevices.parser;

import com.example.managedevices.constant.NtpAttribute;
import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.entity.NtpServer;
import com.example.managedevices.entity.Port;
import com.example.managedevices.utils.OutputUtils;

import java.util.*;

public class OutputParser {
    public static void mapConfigurationToDevice(Map<String,String> configuration, Device device){
        device.setName(configuration.get("Product name"));
        device.setFirmwareVersion(configuration.get("Firmware version"));
        device.setType(configuration.get("Firmware version").substring(0,3));
        device.setSerialNumber(configuration.get("Serial number"));
    }

    public static void mapConfigurationToInterface(String[] configurations,Interface inf){
        inf.setName(configurations[0]);
        inf.setState(Objects.equals(configurations[1], "Enabled"));
        inf.setDhcp(Objects.equals(configurations[2], "Enabled"));
        inf.setIpAddress(Objects.equals(configurations[3], "---") ?"":configurations[3]);
        inf.setNetmask(Objects.equals(configurations[4], "---") ?"":configurations[4]);
        inf.setGateway(Objects.equals(configurations[5], "---") ?"":configurations[5]);
        inf.setInfo(Objects.equals(configurations[6], "---") ?"":configurations[6]);
    }

    public static void mapConfigurationToPort(String[] configurations, Port port){
        port.setConnector(configurations[0].equals("---")?"":configurations[0]);
        port.setPortName(configurations[1]);
        port.setState(configurations[2].equals("Enabled"));
        port.setSpeed(configurations[3]);
        port.setMtu(configurations[4].equals("---")?"":configurations[4]);
        port.setMdi(configurations[5]);
        port.setMacAddress(configurations[6]);
    }

    public static void mapStatusToPort(String status, Port port){
        port.setStatus(status.equals("Enable"));
    }

    public static List<Interface> mapConfigurationToInterfaces(String interfaceConfigurations){
        List<Interface> interfaces=new ArrayList<>();
        String[] configurations= OutputUtils.toArrayConfigurations(interfaceConfigurations);

        for (int i = 2; i < configurations.length; i++) {
            String configuration=configurations[i];
            Interface inf=new Interface();
            String[] properties= OutputUtils.toArrayProperties(configuration);
            mapConfigurationToInterface(properties,inf);
            interfaces.add(inf);
        }
        return interfaces;
    }
    public static List<Port> mapConfigurationToPorts(String portConfigurations){
        List<Port> ports=new ArrayList<>();
        String[] configurations= OutputUtils.toArrayConfigurations(portConfigurations);

        for (int i = 2; i < configurations.length ; i++) {
            String configuration=configurations[i];
            Port port=new Port();
            String[] properties= OutputUtils.toArrayProperties(configuration);
            mapConfigurationToPort(properties,port);
            ports.add(port);
        }
        return ports;
    }

    public NtpServer mapConfigurationToNtp(String ntpConfiguration){
        Map<String,String> ntpMap=OutputUtils.toMapNtpConfiguration(ntpConfiguration);
        NtpServer ntp=new NtpServer();
        ntp.setClient(ntpMap.get(NtpAttribute.CLIENT).equals("Enabled"));
        ntp.setDscp(Integer.parseInt(ntpMap.get(NtpAttribute.DSCP)));
        ntp.setNumberOfMessages(Integer.parseInt(ntpMap.get(NtpAttribute.NUMBER_OF_MESSAGES)));
        ntp.setOffset(Integer.parseInt(ntpMap.get(NtpAttribute.OFFSET)));
        ntp.setSyncStatus(ntpMap.get(NtpAttribute.SYNC_STATUS));
        ntp.setTimeIntervals(ntpMap.get(NtpAttribute.TIME_INTERVAL));
        ntp.setVlanPriority(Integer.parseInt(ntpMap.get(NtpAttribute.VLAN)));

        return ntp;
    }
}
