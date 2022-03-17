package com.example.managedevices.service;

import com.example.managedevices.entity.Ntpserver;

import java.util.List;

public interface NtpServerService {
    List<Ntpserver> getAllNtpservers();
    Ntpserver addNtpserver(Ntpserver ntpServer);
    //Ntpserver updateNtpserver(Ntpserver ntpServer, Long id);
    void deleteNtpserver(Long id);
}
