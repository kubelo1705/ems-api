package com.example.managedevices.constant;

public class Command {
    public static String PORT_CONFIGURE="port show configuration";
    public static String INTERFACE_CONFIGURE="interface show";
    public static String DEVICE_CONFIGURE="board show info";
    public static String FILE_PATH="/home/ddthien/DeviceConfig/newconfig.cfg";
    public static String CONFIGURATION_EXPORT="configuration export scp://ddthien:123456@192.168.81.60"+FILE_PATH;
    public static int DEFAULT_PORT=22;
    public static long DEFAULT_TIMEOUT=1000;
    public static String NTP_CONFIGURE="ntp show";
    public static String ADD_INTERFACE="interface add test address ip_address port port_name state interface_state netmask netmask_address";
}
