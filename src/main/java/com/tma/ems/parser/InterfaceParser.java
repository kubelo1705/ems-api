package com.tma.ems.parser;

import com.tma.ems.constant.CommonValue;
import com.tma.ems.entity.Interface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * parse data from command to interface object
 */
public class InterfaceParser {
    public static void mapArrayConfigurationsToInterface(String[] configurations, Interface inf){
        inf.setName(configurations[0]);
        inf.setState(Objects.equals(configurations[1], CommonValue.ENABLED));
        inf.setDhcp(Objects.equals(configurations[2], CommonValue.ENABLED));
        inf.setIpAddress(Objects.equals(configurations[3], CommonValue.EMPTY) ?"":configurations[3]);
        inf.setNetmask(Objects.equals(configurations[4], CommonValue.EMPTY) ?"":configurations[4]);
        inf.setGateway(Objects.equals(configurations[5], CommonValue.EMPTY) ?"":configurations[5]);
        inf.setInfo(Objects.equals(configurations[6], CommonValue.EMPTY) ?"":configurations[6]);
    }

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
}
