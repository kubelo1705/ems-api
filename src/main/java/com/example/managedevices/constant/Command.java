package com.example.managedevices.constant;

/**
 * Define command access to device
 */
public class Command {
    public static String PORT_CONFIGURE="port show configuration";
    public static String INTERFACE_SHOW ="interface show";
    public static String BOARD_SHOW_INFO ="board show info";
    public static String FILE_PATH="/home/ddthien/DeviceConfig/newconfig.cfg";
    public static String CONFIGURATION_EXPORT="configuration export scp://ddthien:123456@192.168.81.60"+FILE_PATH;
    public static int DEFAULT_PORT=22;
    public static long DEFAULT_TIMEOUT=1000;
    public static String NTP_CONFIGURE="ntp show";
    public static String ADD_INTERFACE="interface add interface_name address ip_address port port_name state interface_state netmask netmask_address";
    public static String ADD_NTP="ntp add ip_address";
    public static String DELETE_INTERFACE="interface delete interface_name";
    public static String EDIT_INTERFACE="interface edit interface_name name new_name address ip_address port port_name state interface_state netmask netmask_address";
    public static String DELETE_NTP="ntp delete ip_address";
}
