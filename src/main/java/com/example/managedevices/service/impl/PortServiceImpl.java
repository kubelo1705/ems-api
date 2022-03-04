package com.example.managedevices.service.impl;

import com.example.managedevices.repo.PortRepo;
import com.example.managedevices.service.PortService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortServiceImpl implements PortService {
    private final PortRepo portRepo;
}
