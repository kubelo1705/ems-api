package com.tma.ems.constant;

/**
 * Define command access to device
 */
public class Command {
    public static String PORT_SHOW_CONFIGURATION = "port show configuration";
    public static String INTERFACE_SHOW = "interface show";
    public static String BOARD_SHOW_INFO = "board show info";
    public static String FILE_PATH = "/home/ddthien/DeviceConfig/newconfig.cfg";
    public static String CONFIGURATION_EXPORT = "configuration export scp://ddthien:123456@192.168.81.60" + FILE_PATH;
    public static String NTP_SHOW = "ntp show";
    public static String INTERFACE_ADD = "interface add interface_name address ip_address port port_name state interface_state netmask netmask_address";
    public static String NTP_ADD = "ntp add ip_address";
    public static String INTERFACE_DELETE = "interface delete interface_name";
    public static String INTERFACE_EDIT = "interface edit interface_name name new_name address ip_address port port_name state interface_state netmask netmask_address";
    public static String NTP_DELETE = "ntp delete ip_address";
}
