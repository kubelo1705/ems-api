package com.example.managedevices.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class OutputUtils {
    public static String formatOutput(String output){
        for (int i = 0; i < 6; i++) {
            output = output.substring(output.indexOf('\n')+1);
        }

        return output.substring(0,output.lastIndexOf("\n")).trim();
    }

    public static Map<String,String>  toMapDeviceConfiguration(String configuration){
        configuration=formatOutput(configuration);
        Map<String,String> map=new HashMap<>();
        for (String s : configuration.split("\n")) {
            String[] pros=s.split(":",2);
            map.put(pros[0].trim(),pros[1].trim());
        }
        return map;
    }

    public static String[] toArrayConfigurations(String configurations){
        configurations=formatOutput(configurations);
        return configurations.split("\n");
    }

    public static String[] toArrayProperties(String properties){
        String formattedConfiguration=properties.replaceAll("\\s+"," ");
        return formattedConfiguration.split(" ");
    }

    public static Map<String,String>  toMapNtpConfiguration(String configuration){
        configuration= configuration.replaceAll("(?m)^[ \t]*\r?\n", "").substring(0,configuration.lastIndexOf(")"));

        Map<String,String> map=new HashMap<>();

        return map;
    }
}
