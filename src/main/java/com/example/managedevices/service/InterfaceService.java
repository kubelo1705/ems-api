package com.example.managedevices.service;

import com.example.managedevices.entity.Interface;
import com.example.managedevices.response.EmsResponse;

import java.util.List;

public interface InterfaceService {
    List<Interface> getAllInterfaces();
    List<Interface> getInterfacesByDeviceId(Long id);
    EmsResponse addInterface(Interface interfaceAdd);
    boolean checkNetmask(Interface interfaceCheck);
    boolean checkIpAddress(Interface interfaceCheck);
    boolean checkDevice(Interface interfaceCheck);
    boolean checkValidInterface(Interface interfaceCheck);
    void deleteInterface(Long id);
    Interface updateInterface(Interface interfaceUpdate,Long id);
}
