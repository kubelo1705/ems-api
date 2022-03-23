package com.example.managedevices.parser;

import com.example.managedevices.constant.CommonValue;
import com.example.managedevices.constant.DeviceAttribute;
import com.example.managedevices.constant.NtpAttribute;
import com.example.managedevices.entity.*;
import java.util.*;

/**
 * Parser data from command
 */
public class CommandParser {
    /**
     * map data from device to device entity
     * @param configuration
     * @param device
     */
    public static void mapConfigurationToDevice(Map<String,String> configuration, Device device){
        device.setName(configuration.get(DeviceAttribute.PRODUCT_NAME));
        device.setFirmwareVersion(configuration.get(DeviceAttribute.FIRMWARE_VERSION));
        device.setType(configuration.get(DeviceAttribute.FIRMWARE_VERSION).substring(0,3));
        device.setSerialNumber(configuration.get(DeviceAttribute.SERIAL_NUMBER));
        device.setMacAddress(configuration.get(DeviceAttribute.MAC_BASE_ADDRESS));
        device.setUnitIdentifier(configuration.get(DeviceAttribute.UNIT_IDENTIFIER));
    }

    /**
     * map data from device to interface entity
     * @param configurations
     * @param inf
     */
    public static void mapConfigurationToInterface(String[] configurations,Interface inf){
        inf.setName(configurations[0]);
        inf.setState(Objects.equals(configurations[1], CommonValue.ENABLED));
        inf.setDhcp(Objects.equals(configurations[2], CommonValue.ENABLED));
        inf.setIpAddress(Objects.equals(configurations[3], CommonValue.EMPTY) ?"":configurations[3]);
        inf.setNetmask(Objects.equals(configurations[4], CommonValue.EMPTY) ?"":configurations[4]);
        inf.setGateway(Objects.equals(configurations[5], CommonValue.EMPTY) ?"":configurations[5]);
        inf.setInfo(Objects.equals(configurations[6], CommonValue.EMPTY) ?"":configurations[6]);
    }

    /**
     * map data from device to port entity
     * @param configurations
     * @param port
     */
    public static void mapConfigurationToPort(String[] configurations, Port port){
        port.setConnector(configurations[0].equals(CommonValue.EMPTY)?"":configurations[0]);
        port.setPortName(configurations[1]);
        port.setState(configurations[2].equals(CommonValue.ENABLED));
        port.setSpeed(configurations[3].equals(CommonValue.EMPTY)?"":configurations[3]);
        port.setMtu(configurations[4].equals(CommonValue.EMPTY)?"":configurations[4]);
        port.setMdi(configurations[5].equals(CommonValue.EMPTY)?"":configurations[5]);
        port.setMacAddress(configurations[6]);
    }

    /**
     * map data from device to list interfaces
     * @param interfaceConfigurations
     * @return
     */
    public static List<Interface> mapConfigurationToInterfaces(String interfaceConfigurations){
        List<Interface> interfaces=new ArrayList<>();
        String[] configurations= toArrayConfigurations(interfaceConfigurations);

        for (int i = 2; i < configurations.length; i++) {
            String configuration=configurations[i];
            Interface inf=new Interface();
            String[] properties= toArrayProperties(configuration);
            mapConfigurationToInterface(properties,inf);
            interfaces.add(inf);
        }
        return interfaces;
    }

    /**
     * map data from device to list ports
     * @param portConfigurations
     * @return
     */
    public static List<Port> mapConfigurationToPorts(String portConfigurations){
        List<Port> ports=new ArrayList<>();
        String[] configurations= toArrayConfigurations(portConfigurations);

        for (int i = 2; i < configurations.length ; i++) {
            String configuration=configurations[i];
            Port port=new Port();
            String[] properties= toArrayProperties(configuration);
            mapConfigurationToPort(properties,port);
            ports.add(port);
        }
        return ports;
    }

    /**
     * map data from device to ntp server
     * @param ntpConfiguration
     * @return
     */
    public static Ntpserver mapConfigurationToNtp(String ntpConfiguration){
        Map<String,String> ntpMap=toMapNtpConfiguration(ntpConfiguration);
        Ntpserver ntp=new Ntpserver();
        ntp.setClient(ntpMap.get(NtpAttribute.CLIENT).equals(CommonValue.ENABLED));
        ntp.setDscp(Integer.parseInt(ntpMap.get(NtpAttribute.DSCP)));
        ntp.setNumberOfMessages(Integer.parseInt(ntpMap.get(NtpAttribute.NUMBER_OF_MESSAGES)));
        ntp.setOffset(Integer.parseInt(ntpMap.get(NtpAttribute.OFFSET)));
        ntp.setSyncStatus(ntpMap.get(NtpAttribute.SYNC_STATUS));
        ntp.setTimeIntervals(ntpMap.get(NtpAttribute.TIME_INTERVAL));
        ntp.setVlanPriority(Integer.parseInt(ntpMap.get(NtpAttribute.VLAN)));

        Set<Ntpaddress> enabledAddress=new HashSet<>();
        if(!ntpMap.get(NtpAttribute.ENABLED_ADDRESS).isBlank()) {
            Arrays.stream((ntpMap.get(NtpAttribute.ENABLED_ADDRESS)).split(" ")).forEach(address -> {
                enabledAddress.add(new Ntpaddress(address, true, ntp));
            });
        }

        Set<Ntpaddress> disabledAddress=new HashSet<>();
        if(!ntpMap.get(NtpAttribute.DISABLED_ADDRESS).isBlank()) {
            Arrays.stream((ntpMap.get(NtpAttribute.DISABLED_ADDRESS)).split(" ")).forEach(address -> {
                disabledAddress.add(new Ntpaddress(address, false, ntp));
            });
        }

        enabledAddress.addAll(disabledAddress);
        ntp.setNtpaddresses(enabledAddress);

        return ntp;
    }

    /**
     * get port name from interface configurations
     * @param configurations
     * @return
     */
    public static String getPortNameFromInterfaceDetails(String configurations){
        String[] pros=configurations.split("\\n");
        for (String pro : pros) {
            if(pro.contains("On port")){
                System.out.println(pro);
                String[] port=pro.split(":");
                return port[1];
            }
        }
        return null;
    }

    /**
     * format output of command. only get main data
     * @param output
     * @return
     */
    public static String formatOutput(String output){
        return output.substring(output.indexOf("\r\n\r\n",output.indexOf("\r\n\r\n")+1),output.lastIndexOf("\n")).trim();
    }

    /**
     * from data to map devices configuration
     * @param configuration
     * @return
     */
    public static Map<String,String>  toMapDeviceConfiguration(String configuration){
        configuration=formatOutput(configuration);
        Map<String,String> map=new HashMap<>();
        for (String s : configuration.split("\n")) {
            String[] pros=s.split(":",2);
            map.put(pros[0].trim(),pros[1].trim());
        }
        return map;
    }

    /**
     * split data from many line to array line data
     * @param configurations
     * @return
     */
    public static String[] toArrayConfigurations(String configurations){
        configurations=formatOutput(configurations);
        return configurations.split("\n");
    }

    /**
     * from line with many data to array specify data
     * @param properties
     * @return
     */
    public static String[] toArrayProperties(String properties){
        String formattedConfiguration=properties.replaceAll("\\s+"," ");
        return formattedConfiguration.split(" ");
    }

    /**
     * from data to map data ntp configuration
     * @param configuration
     * @return
     */
    public static Map<String,String>  toMapNtpConfiguration(String configuration){

        configuration=formatOutput(configuration).replaceAll("(?m)^[ \t]*\r?\n", "");
        String formatedConfiguration = configuration.substring(0, configuration.lastIndexOf(NtpAttribute.END));
        String[] enabledAddress= formatedConfiguration.substring(formatedConfiguration.indexOf(NtpAttribute.ENABLED_ADDRESS),formatedConfiguration.indexOf(NtpAttribute.DISABLED_ADDRESS)).split(":");
        String[] disabledAddress=formatedConfiguration.substring(formatedConfiguration.indexOf(NtpAttribute.DISABLED_ADDRESS)).split(":");

        Map<String,String> map=new HashMap<>();

        String[] attributes=formatedConfiguration.substring(0,formatedConfiguration.indexOf(NtpAttribute.ENABLED_ADDRESS)).split("\n");
        for (String attribute : attributes) {
            String[] keyValue=attribute.split(":");
            map.put(keyValue[0].trim(),keyValue[1].trim());
        }

        map.put(enabledAddress[0],addressesToStringAddress(enabledAddress[1]));
        map.put(disabledAddress[0],addressesToStringAddress(disabledAddress[1]));

        return map;
    }

    /**
     * format addresses from output of ntp configuration to array address
     * @param addresses
     * @return
     */
    public static String addressesToStringAddress(String addresses){
        String result="";
        for (String s : addresses.split("\n")) {
            if(s.indexOf("(")!=-1){
                s=s.substring(0,s.indexOf("("));
            }
            result=result.trim()+" "+s.trim();
        }
        return result.trim();
    }

    /**
     * check if this output is error output or not
     * @param serialDevice
     * @param command
     * @param output
     * @return
     */
    public static boolean isErrorOutput(String serialDevice,String command,String output){
        output=output.substring(output.indexOf(serialDevice)+serialDevice.length()+1,output.lastIndexOf(serialDevice));
        output=output.replace("\n","").replace("\r","").trim();
        return !(output.length()==command.length());
    }
}
