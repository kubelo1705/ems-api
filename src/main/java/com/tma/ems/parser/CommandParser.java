package com.tma.ems.parser;

/**
 * Parse data from command
 */
public class CommandParser {

     /**
     * format output of command. only get main data
     * @param output
     * @return
     */
    public static String formatOutput(String output){
        return output.substring(output.indexOf("\r\n\r\n",output.indexOf("\r\n\r\n")+1),output.lastIndexOf("\n")).trim();
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
