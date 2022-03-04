package com.example.managedevices.vadilation;

public class EntityValidator {
    public static boolean isValidIp(String ip){
        String[] subIps=ip.split("\\.");
        try{
            for (String subIp : subIps) {
                int subIpInt = Integer.parseInt(subIp);
                if (subIpInt < 0 && subIpInt >= 255) {
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean isValidNetmask(String netmask){
        String[] subIps=netmask.split("\\.");
        try{
            for (String subIp : subIps) {
                int subIpInt = Integer.parseInt(subIp);
                if (subIpInt != 0 || subIpInt != 255) {
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

}
