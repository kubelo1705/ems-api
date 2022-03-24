package com.tma.ems.service;

import com.tma.ems.entity.Device;
import com.tma.ems.entity.Interface;
import com.tma.ems.entity.Port;

import java.util.List;
import java.util.Map;

public interface InterfaceService {
    /**
     * get interfaces of a managed device by id
     * @param id
     * @return
     */
    List<Interface> getInterfacesByDeviceId(Long id);

    /**
     * add a new interface to a managed device an save to database
     * @param idDevice
     * @param map
     * @return
     */
    Interface addInterface(Long idDevice,Map<String,Object> map);

    /**
     * delete an interface of an managed device and delete it in database
     * @param idDevice
     * @param interfaceName
     */
    void deleteInterface(Long idDevice,String interfaceName);

    /**
     * update an interface of an managed device and update it in database
     * @param idDevice
     * @param map
     * @return
     */
    Interface updateInterface(Long idDevice,Map<String,Object> map);

    /**
     * create new interface with all basic params
     * @param map
     * @param port
     * @param device
     * @return
     */
    Interface create(Map<String,Object> map, Port port, Device device);
}
