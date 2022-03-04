package com.example.managedevices.service;

import com.example.managedevices.entity.Interface;

import java.util.List;

public interface InterfaceService {
    List<Interface> getAllInterface();
    List<Interface> getInterfacesByDeviceId(Long id);
    Interface addInterface(Interface interfaceAdd);
    boolean checkNetmask(Interface interfaceCheck);
    boolean checkIpAddress(Interface interfaceCheck);
    boolean checkDevice(Interface interfaceCheck);
    boolean CheckInterfaceId(Long id);
    boolean checkValidInterface(Interface interfaceCheck);
    void deleteById(Long id);
}
