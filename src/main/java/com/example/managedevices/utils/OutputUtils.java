package com.example.managedevices.utils;

import com.example.managedevices.constant.NtpAttribute;

import java.util.HashMap;
import java.util.Map;

public class OutputUtils {
    public static String formatOutput(String output){
        return output.substring(output.indexOf("\r\n\r\n",output.indexOf("\r\n\r\n")+1),output.lastIndexOf("\n")).trim();
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

        configuration=OutputUtils.formatOutput(configuration).replaceAll("(?m)^[ \t]*\r?\n", "");
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

    public static boolean isErrorOutput(String serialDevice,String command,String output){
        output=output.substring(output.indexOf(serialDevice)+serialDevice.length()+1,output.lastIndexOf(serialDevice));
        output=output.replace("\n","").replace("\r","").trim();
        return !(output.length()==command.length());
    }
}
