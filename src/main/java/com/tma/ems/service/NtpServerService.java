package com.tma.ems.service;

import com.tma.ems.entity.Ntpaddress;
import com.tma.ems.entity.Ntpserver;

public interface NtpServerService {
    /**
     * get ntp server of a managed device by id
     * @param id
     * @return
     */
    Ntpserver getNtpserverByDeviceId(Long id);

    /**
     * add a new ntp server to a managed device
     * @param idDevice
     * @param ntpaddress
     * @return
     */
    Ntpaddress addNtpserver(Long idDevice, Ntpaddress ntpaddress);

    /**
     * delete a existed ntp server of a manage device and update to database
     * @param idDevice
     * @param address
     */
    void deleteNtpserver(Long idDevice,String address);
}
