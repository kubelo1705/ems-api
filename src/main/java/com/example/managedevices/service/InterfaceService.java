package com.example.managedevices.service;

import com.example.managedevices.entity.Interface;
import com.example.managedevices.response.EmsResponse;

import java.util.List;

public interface InterfaceService {
    List<Interface> getAllInterfaces();
    List<Interface> getInterfacesByDeviceId(Long id);
    Interface addInterface(Interface interfaceAdd,Long idDevice);
    boolean checkNetmask(Interface interfaceCheck);
    boolean checkIpAddress(Interface interfaceCheck);
    boolean checkValidInterface(Interface interfaceCheck);
    void deleteInterface(Long idDevice,String interfaceName);
    Interface updateInterface(String interfaceName,Interface interfaceUpdate,Long idDevice);
}
