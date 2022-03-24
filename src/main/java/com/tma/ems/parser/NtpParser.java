package com.tma.ems.parser;

import com.tma.ems.constant.CommonValue;
import com.tma.ems.entity.Ntpaddress;
import com.tma.ems.entity.Ntpserver;

import java.util.*;

/**
 * Parse output command to ntp server object
 */
public class NtpParser {
    private static final String CLIENT = "NTP client";
    private static final String OFFSET = "TAI offset";
    private static final String DSCP = "DSCP";
    private static final String VLAN = "VLAN priority";
    private static final String SYNC_STATUS = "Sync status";
    private static final String TIME_INTERVAL = "Time interval";
    private static final String NUMBER_OF_MESSAGES = "Number of Messages";
    private static final String ENABLED_ADDRESS = "Enabled server list";
    private static final String DISABLED_ADDRESS = "Disabled server list";
    private static final String END = "Current date";

    /**
     * from data to map data ntp configuration
     * @param outputCommand
     * @return
     */
    public static Map<String, String> convertOutputCommandToMap(String outputCommand) {

        outputCommand = CommandParser.formatOutput(outputCommand).replaceAll("(?m)^[ \t]*\r?\n", "");
        String formattedOutput = outputCommand.substring(0, outputCommand.lastIndexOf(END));

        String enabledAddress = getEnabledAddressFromFormattedOutputCommand(formattedOutput, ENABLED_ADDRESS, DISABLED_ADDRESS);
        String disabledAddress = getDisabledAddressFromFormattedOutputCommand(formattedOutput, DISABLED_ADDRESS);

        Map<String, String> map = new HashMap<>();

        String[] attributes = formattedOutput.substring(0, formattedOutput.indexOf(ENABLED_ADDRESS)).split("\n");
        for (String attribute : attributes) {
            String[] keyValue = attribute.split(":");
            map.put(keyValue[0].trim(), keyValue[1].trim());
        }

        map.put(ENABLED_ADDRESS, enabledAddress);
        map.put(DISABLED_ADDRESS, disabledAddress);

        return map;
    }

    /**
     * format addresses from output of ntp configuration to array address
     * @param address
     * @return
     */
    public static String formatStringAddress(String address) {
        String result = "";
        for (String s : address.split("\n")) {
            if (s.indexOf("(") != -1) {
                s = s.substring(0, s.indexOf("("));
            }
            result = result.trim() + " " + s.trim();
        }
        return result.trim();
    }

    /**
     * map data from device to ntp server
     * @param outputCommand
     * @return
     */
    public static Ntpserver convertOutputCommandToNtp(String outputCommand) {
        Map<String, String> ntpMap = convertOutputCommandToMap(outputCommand);

        Ntpserver ntp = mapConfigurationToNtpserver(ntpMap);

        Set<Ntpaddress> enabledAddress = convertAddressToSet(ntpMap.get(ENABLED_ADDRESS), ntp);

        Set<Ntpaddress> disabledAddress = convertAddressToSet(ntpMap.get(DISABLED_ADDRESS), ntp);

        enabledAddress.addAll(disabledAddress);
        ntp.setNtpaddresses(enabledAddress);

        return ntp;
    }

    /**
     * Return set address of a ntp server
     *
     * @param addresses
     * @param ntp
     * @return
     */
    public static Set<Ntpaddress> convertAddressToSet(String addresses, Ntpserver ntp) {
        Set<Ntpaddress> ntpaddresses = new HashSet<>();
        if (!addresses.isBlank()) {
            Arrays.stream((addresses).split(" ")).forEach(address -> {
                ntpaddresses.add(new Ntpaddress(address, false, ntp));
            });
        }
        return ntpaddresses;
    }

    /**
     * map configuarions to ntp server
     * @param configurations
     * @return
     */
    public static Ntpserver mapConfigurationToNtpserver(Map<String, String> configurations) {
        Ntpserver ntp = new Ntpserver();

        ntp.setClient(configurations.get(CLIENT).equals(CommonValue.ENABLED));
        ntp.setDscp(Integer.parseInt(configurations.get(DSCP)));
        ntp.setNumberOfMessages(Integer.parseInt(configurations.get(NUMBER_OF_MESSAGES)));
        ntp.setOffset(Integer.parseInt(configurations.get(OFFSET)));
        ntp.setSyncStatus(configurations.get(SYNC_STATUS));
        ntp.setTimeIntervals(configurations.get(TIME_INTERVAL));
        ntp.setVlanPriority(Integer.parseInt(configurations.get(VLAN)));

        return ntp;
    }

    /**
     * get address from lines in command and transfer to string with one line and
     * @param formattedOutput
     * @param from
     * @param to
     * @return
     */
    public static String getEnabledAddressFromFormattedOutputCommand(String formattedOutput, String from, String to) {
        String[] arrayAddresses;
        String addresses;

        arrayAddresses = formattedOutput.substring(formattedOutput.indexOf(from), formattedOutput.indexOf(to)).split(":");

        addresses = formatStringAddress(arrayAddresses[1]);
        return addresses;
    }

    /**
     * get address from lines in command and transfer to string with one line and
     * @param formattedOutput
     * @param from
     * @return
     */
    public static String getDisabledAddressFromFormattedOutputCommand(String formattedOutput, String from) {
        String[] arrayAddresses;
        String addresses;

        arrayAddresses = formattedOutput.substring(formattedOutput.indexOf(from)).split(":");

        addresses = formatStringAddress(arrayAddresses[1]);
        return addresses;
    }
}
