package com.example.managedevices.controller;

import com.example.managedevices.service.PortService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("port")
public class PortController {
    PortService portService;
    @GetMapping
    public ResponseEntity<?> getAllPorts(){
        return ResponseEntity.ok(portService.getAllPorts());
    }
}
