package com.tma.ems.parser;

import com.tma.ems.entity.Device;

import java.util.HashMap;
import java.util.Map;

/**
 * parse data from command to existed device
 */
public class DeviceParser {
    private static final String PRODUCT_NAME="Product name";
    private static final String MAC_BASE_ADDRESS="MAC base address";
    private static final String UNIT_IDENTIFIER="Unit identifier";
    private static final String FIRMWARE_VERSION="Firmware version";
    private static final String SERIAL_NUMBER="Serial number";

    /**
     * from data to map devices configuration
     * @param output
     * @return
     */
    public static Map<String,String> convertOutputCommandToMapConfiguration(String output){
        output=CommandParser.formatOutput(output);
        Map<String,String> map=new HashMap<>();
        for (String s : output.split("\n")) {
            String[] pros=s.split(":",2);
            map.put(pros[0].trim(),pros[1].trim());
        }
        return map;
    }
    /**
     * map data from device to device entity
     * @param outputCommand
     * @param device
     */
    public static void mapOutputCommandToDevice(String outputCommand, Device device){
        Map<String,String> configurations=convertOutputCommandToMapConfiguration(outputCommand);
        device.setName(configurations.get(PRODUCT_NAME));
        device.setFirmwareVersion(configurations.get(FIRMWARE_VERSION));
        device.setType(configurations.get(FIRMWARE_VERSION).substring(0,3));
        device.setSerialNumber(configurations.get(SERIAL_NUMBER));
        device.setMacAddress(configurations.get(MAC_BASE_ADDRESS));
        device.setUnitIdentifier(configurations.get(UNIT_IDENTIFIER));
    }
}
