package com.example.managedevices.service;

import com.example.managedevices.entity.NtpServer;

import java.util.List;

public interface NtpServerService {
    List<NtpServer> getAllNtpservers();
    NtpServer addNtpserver(NtpServer ntpServer);
    NtpServer updateNtpserver(NtpServer ntpServer,Long id);
    void deleteNtpserver(Long id);
}
