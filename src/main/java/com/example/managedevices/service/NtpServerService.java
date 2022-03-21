package com.example.managedevices.service;

import com.example.managedevices.entity.Ntpaddress;
import com.example.managedevices.entity.Ntpserver;

import java.util.List;

public interface NtpServerService {
    Ntpserver getNtpserverByDeviceId(Long id);
    Ntpaddress addNtpserver(Long idDevice, Ntpaddress ntpaddress);
    //Ntpserver updateNtpserver(Ntpserver ntpServer, Long id);
    void deleteNtpserver(Long idDevice,String address);
}
