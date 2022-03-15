package com.example.managedevices.controller;

import com.example.managedevices.service.PortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/so/ports")
public class PortController {
    @Autowired
    PortService portService;
    
    @GetMapping
    public ResponseEntity<?> getAllPorts(){
        return ResponseEntity.ok(portService.getAllPorts());
    }
}
