package com.example.managedevices.service;

import com.example.managedevices.entity.Interface;
import com.example.managedevices.response.EmsResponse;

import java.util.List;
import java.util.Map;

public interface InterfaceService {
    List<Interface> getInterfacesByDeviceId(Long id);
    Interface addInterface(Long idDevice,Map<String,Object> map);
    void deleteInterface(Long idDevice,String interfaceName);
    Interface updateInterface(Long idDevice,Map<String,Object> map);
}
