package com.tma.ems.service;

import com.tma.ems.entity.Ntpaddress;
import com.tma.ems.entity.Ntpserver;

public interface NtpServerService {
    Ntpserver getNtpserverByDeviceId(Long id);
    Ntpaddress addNtpserver(Long idDevice, Ntpaddress ntpaddress);
    //Ntpserver updateNtpserver(Ntpserver ntpServer, Long id);
    void deleteNtpserver(Long idDevice,String address);
}
