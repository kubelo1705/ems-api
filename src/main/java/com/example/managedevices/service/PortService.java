package com.example.managedevices.service;

import com.example.managedevices.entity.NtpServer;
import com.example.managedevices.entity.Port;

import java.util.List;

public interface PortService {
    List<Port> getAllPorts();
    Port addNtpserver(NtpServer ntpServer);
}
