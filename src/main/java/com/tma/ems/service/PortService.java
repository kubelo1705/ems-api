package com.tma.ems.service;

import com.tma.ems.entity.Port;

import java.util.List;

public interface PortService {
    List<Port> getPortByDeviceId(Long id);
}
