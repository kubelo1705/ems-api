package com.tma.ems.service;

import com.tma.ems.entity.Interface;

import java.util.List;
import java.util.Map;

public interface InterfaceService {
    List<Interface> getInterfacesByDeviceId(Long id);
    Interface addInterface(Long idDevice,Map<String,Object> map);
    void deleteInterface(Long idDevice,String interfaceName);
    Interface updateInterface(Long idDevice,Map<String,Object> map);
}
