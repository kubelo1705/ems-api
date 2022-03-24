package com.tma.ems.parser;

import com.tma.ems.entity.Interface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * parse data from command to interface object
 */
public class InterfaceParser {
    private static final String EMPTY="---";
    private static final String ENABLED="enabled";
    private static final String DISABLED="disabled";

    /**
     * map configurations from array to interface
     * @param configurations
     * @param inf
     */
    public static void mapArrayConfigurationsToInterface(String[] configurations, Interface inf){
        inf.setName(configurations[0]);
        inf.setState(configurations[1].equalsIgnoreCase(ENABLED));
        inf.setDhcp(configurations[2].equalsIgnoreCase(ENABLED));
        inf.setIpAddress(configurations[3].equalsIgnoreCase(EMPTY) ?"":configurations[3]);
        inf.setNetmask(configurations[4].equalsIgnoreCase(EMPTY) ?"":configurations[4]);
        inf.setGateway(configurations[5].equalsIgnoreCase(EMPTY) ?"":configurations[5]);
        inf.setInfo(configurations[6].equalsIgnoreCase(EMPTY) ?"":configurations[6]);
    }

    /**
     * parse output command to list interfaces
     * @param outputCommand
     * @return
     */
    public static List<Interface> convertOutputCommandToInterfaces(String outputCommand){
        List<Interface> interfaces=new ArrayList<>();
        String[] configurations= CommandParser.toArrayConfigurations(outputCommand);

        for (int i = 2; i < configurations.length; i++) {
            String configuration=configurations[i];
            Interface inf=new Interface();
            String[] properties= CommandParser.toArrayProperties(configuration);
            mapArrayConfigurationsToInterface(properties,inf);
            interfaces.add(inf);
        }
        return interfaces;
    }

    /**
     * get port name from interface configurations
     * @param outputCommand
     * @return
     */
    public static String getPortNameFromOutputCommand(String outputCommand){
        String[] pros=outputCommand.split("\\n");
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
     * map attribute to interface object
     * @param baseCommand
     * @param map
     * @return
     */
    public static String parseMapToCommand(String baseCommand, Map<String, Object> map) {
        baseCommand = baseCommand.replace("interface_name", map.get("interface_name") != null ? map.get("interface_name").toString() : "");

        if (map.get("new_interface_name") != null) {
            baseCommand = baseCommand.replace("new_name", map.get("new_name").toString());
        }

        if (map.get("ip_address") == null) {
            baseCommand = baseCommand.replace("address ip_address", "");
        } else {
            baseCommand = baseCommand.replace("ip_address", map.get("ip_address").toString());
        }

        if (map.get("port_name") == null) {
            baseCommand = baseCommand.replace("port port_name", "");
        } else {
            baseCommand = baseCommand.replace("port_name", map.get("port_name").toString());
        }

        if (map.get("state") == null) {
            baseCommand = baseCommand.replace("state interface_state", "");
        } else {
            baseCommand = baseCommand.replace("interface_state", map.get("state").toString());
        }

        if (map.get("netmask") == null) {
            baseCommand = baseCommand.replace("netmask netmask_address", "");
        } else {
            baseCommand = baseCommand.replace("netmask_address", map.get("netmask").toString());
        }
        return baseCommand.trim();
    }

    /**
     * paree data from map to Interface
     * @param map
     * @param inf
     */
    public static void parseMapToInterface(Map<String,Object> map, Interface inf){
        if(map.get("interface_name")!=null){
            inf.setName(map.get("interface_name").toString());
        }
        if (map.get("new_name") != null) {
            inf.setName(map.get("new_name").toString());
        }
        if (map.get("ip_address") != null) {
            inf.setIpAddress(map.get("ip_address").toString());
        }
        if (map.get("netmask") != null) {
            inf.setNetmask(map.get("netmask").toString());
        }
        if (map.get("interface_state") != null) {
            inf.setState(map.get("interface_state").toString().equalsIgnoreCase("enable"));
        }
    }
}
