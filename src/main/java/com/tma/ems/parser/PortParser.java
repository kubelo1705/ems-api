package com.tma.ems.parser;

import com.tma.ems.entity.Port;

import java.util.ArrayList;
import java.util.List;

/**
 * parse data from command to port object
 */
public class PortParser {
    private static final String ENABLED="Enabled";
    private static final String EMPTY="---";
    private static final String DISABLED="Disabled";
    /**
     * map data from device to port object
     * @param configurations
     * @param port
     */
    public static void mapConfigurationToPort(String[] configurations, Port port){
        port.setConnector(configurations[0].equalsIgnoreCase(EMPTY)?"":configurations[0]);
        port.setPortName(configurations[1]);
        port.setState(configurations[2].equalsIgnoreCase(ENABLED));
        port.setSpeed(configurations[3].equalsIgnoreCase(EMPTY)?"":configurations[3]);
        port.setMtu(configurations[4].equalsIgnoreCase(EMPTY)?"":configurations[4]);
        port.setMdi(configurations[5].equalsIgnoreCase(EMPTY)?"":configurations[5]);
        port.setMacAddress(configurations[6]);
    }

    /**
     * map data from device to list ports
     * @param outputCommand
     * @return
     */
    public static List<Port> convertOutputCommandToPorts(String outputCommand){
        List<Port> ports=new ArrayList<>();
        String[] configurations= convertOutputCommandToArrayConfigurations(outputCommand);

        for (int i = 2; i < configurations.length ; i++) {
            String configuration=configurations[i];
            Port port=new Port();
            String[] properties= convertConfigurationToArrayProperties(configuration);
            mapConfigurationToPort(properties,port);
            ports.add(port);
        }

        return ports;
    }

    /**
     * convert output command to array configurations. a line in output command is a configuration
     * @param outputCommand
     * @return
     */
    public static String[] convertOutputCommandToArrayConfigurations(String outputCommand){
        return CommandParser.toArrayConfigurations(outputCommand);
    }

    /**
     * convert a configuration to array properties
     * @param configuration
     * @return
     */
    public static String[] convertConfigurationToArrayProperties(String configuration){
        return CommandParser.toArrayProperties(configuration);
    }
}
