package com.tma.ems.utils;

import com.tma.ems.entity.Credential;

/**
 * validate data input
 */
public class ValidationUtils {
    /**
     * valid ipv4
     */
    public static boolean isValidIp(String ip) {
        String[] subIps = ip.split("\\.");
        try {
            for (String subIp : subIps) {
                int subIpInt = Integer.parseInt(subIp);
                if (subIpInt < 0 && subIpInt >= 255) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * valid subnet mask ip
     */
    public static boolean isValidNetmask(String netmask) {
        String[] subIps = netmask.split("\\.");
        try {
            for (String subIp : subIps) {
                int subIpInt = Integer.parseInt(subIp);
                if (subIpInt != 0 && subIpInt != 255) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * valid credential
     */
    public static boolean isValidCredential(Credential credential) {
        String name = credential.getName();
        String username = credential.getUsername();
        String password = credential.getPassword();
        return name != null && username != null && !username.contains(" ") && password != null && !password.contains(" ");
    }

}
