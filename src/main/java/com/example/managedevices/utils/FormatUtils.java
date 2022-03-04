package com.example.managedevices.utils;

import java.util.HashMap;
import java.util.Map;

public class FormatUtils {
    public static String formatOutput(String output,String command){
        return output.substring(output.lastIndexOf(command) + command.length(),output.lastIndexOf("\n")).trim();
    }

    public static Map<String,String> toMapDeviceConfiguration(String configuration){
        Map<String,String> map=new HashMap<>();
        for (String s : configuration.split("\n")) {
            String[] pros=s.split(":",2);
            map.put(pros[0].trim(),pros[1].trim());
        }
        return map;
    }

    public static String[] toInterfaceConfiguration(String configuration){
        String formattedConfiguration=configuration.replaceAll("\\s+"," ");
        String[] properties=formattedConfiguration.split(" ");
        return properties;
    }
}
