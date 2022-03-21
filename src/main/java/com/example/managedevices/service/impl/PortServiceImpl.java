package com.example.managedevices.service.impl;

import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.entity.Port;
import com.example.managedevices.repository.PortRepository;
import com.example.managedevices.service.PortService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortServiceImpl implements PortService {
    private final PortRepository portRepo;

    @Override
    public List<Port> getAllPorts() {
        return portRepo.findAll();
    }

    @Override
    public List<Port> getPortByDeviceId(Long id) {
        return portRepo.findPortsByDevice_Id(id);
    }

}
