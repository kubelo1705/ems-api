package com.tma.ems.controller;

import com.tma.ems.constant.Message;
import com.tma.ems.service.PortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/ports")
public class PortController {
    @Autowired
    PortService portService;

    @GetMapping("/device/{id}")
    public ResponseEntity<?> getPortsByDeviceId(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            return ResponseEntity.ok(portService.getPortByDeviceId(id.get()));
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }
}
