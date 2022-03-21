package com.example.managedevices.service;

import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.entity.Port;

import java.util.List;

public interface PortService {
    List<Port> getAllPorts();
    List<Port> getPortByDeviceId(Long id);
}
