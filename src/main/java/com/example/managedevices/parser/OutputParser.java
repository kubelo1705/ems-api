package com.example.managedevices.parser;

import com.example.managedevices.constant.CommonValue;
import com.example.managedevices.constant.DeviceAttribute;
import com.example.managedevices.constant.NtpAttribute;
import com.example.managedevices.entity.*;
import com.example.managedevices.utils.OutputUtils;

import java.util.*;

public class OutputParser {
    public static void mapConfigurationToDevice(Map<String,String> configuration, Device device){
        device.setName(configuration.get(DeviceAttribute.PRODUCT_NAME));
        device.setFirmwareVersion(configuration.get(DeviceAttribute.FIRMWARE_VERSION));
        device.setType(configuration.get(DeviceAttribute.FIRMWARE_VERSION).substring(0,3));
        device.setSerialNumber(configuration.get(DeviceAttribute.SERIAL_NUMBER));
        device.setMacAddress(configuration.get(DeviceAttribute.MAC_BASE_ADDRESS));
        device.setUnitIdentifier(configuration.get(DeviceAttribute.UNIT_IDENTIFIER));
    }

    public static void mapConfigurationToInterface(String[] configurations,Interface inf){
        inf.setName(configurations[0]);
        inf.setState(Objects.equals(configurations[1], CommonValue.ENABLED));
        inf.setDhcp(Objects.equals(configurations[2], CommonValue.ENABLED));
        inf.setIpAddress(Objects.equals(configurations[3], CommonValue.EMPTY) ?"":configurations[3]);
        inf.setNetmask(Objects.equals(configurations[4], CommonValue.EMPTY) ?"":configurations[4]);
        inf.setGateway(Objects.equals(configurations[5], CommonValue.EMPTY) ?"":configurations[5]);
        inf.setInfo(Objects.equals(configurations[6], CommonValue.EMPTY) ?"":configurations[6]);
    }

    public static void mapConfigurationToPort(String[] configurations, Port port){
        port.setConnector(configurations[0].equals(CommonValue.EMPTY)?"":configurations[0]);
        port.setPortName(configurations[1]);
        port.setState(configurations[2].equals(CommonValue.ENABLED));
        port.setSpeed(configurations[3]);
        port.setMtu(configurations[4].equals(CommonValue.EMPTY)?"":configurations[4]);
        port.setMdi(configurations[5]);
        port.setMacAddress(configurations[6]);
    }

    public static void mapStatusToPort(String status, Port port){
        port.setStatus(status.equals(CommonValue.ENABLED));
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

    public static Ntpserver mapConfigurationToNtp(String ntpConfiguration){
        Map<String,String> ntpMap=OutputUtils.toMapNtpConfiguration(ntpConfiguration);
        Ntpserver ntp=new Ntpserver();
        ntp.setClient(ntpMap.get(NtpAttribute.CLIENT).equals(CommonValue.ENABLED));
        ntp.setDscp(Integer.parseInt(ntpMap.get(NtpAttribute.DSCP)));
        ntp.setNumberOfMessages(Integer.parseInt(ntpMap.get(NtpAttribute.NUMBER_OF_MESSAGES)));
        ntp.setOffset(Integer.parseInt(ntpMap.get(NtpAttribute.OFFSET)));
        ntp.setSyncStatus(ntpMap.get(NtpAttribute.SYNC_STATUS));
        ntp.setTimeIntervals(ntpMap.get(NtpAttribute.TIME_INTERVAL));
        ntp.setVlanPriority(Integer.parseInt(ntpMap.get(NtpAttribute.VLAN)));

        Set<Ntpaddress> enabledAddress=new HashSet<>();
        Arrays.stream((ntpMap.get(NtpAttribute.ENABLED_ADDRESS)).split(" ")).forEach(address->{
            enabledAddress.add(new Ntpaddress(address,true,ntp));
        });

        Set<Ntpaddress> disabledAddress=new HashSet<>();
        Arrays.stream((ntpMap.get(NtpAttribute.DISABLED_ADDRESS)).split(" ")).forEach(address->{
            disabledAddress.add(new Ntpaddress(address,false,ntp));
        });

        enabledAddress.addAll(disabledAddress);
        ntp.setNtpaddresses(enabledAddress);

        return ntp;
    }
}
