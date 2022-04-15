package com.tma.ems.service;

import com.tma.ems.entity.Port;

import java.util.List;

public interface PortService {
    /**
     * get port of a managed device
     *
     * @param id
     * @return
     */
    List<Port> getPortByDeviceId(Long id);
}
