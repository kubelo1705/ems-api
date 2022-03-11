package com.example.managedevices.service.impl;

import com.example.managedevices.entity.NtpServer;
import com.example.managedevices.entity.Port;
import com.example.managedevices.repo.PortRepo;
import com.example.managedevices.service.PortService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PortServiceImpl implements PortService {
    private final PortRepo portRepo;

    @Override
    public List<Port> getAllPorts() {
        return portRepo.findAll();
    }

    @Override
    public Port addNtpserver(NtpServer ntpServer) {
        return null;
    }

}
