package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Port;
import com.example.managedevices.service.PortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/so/ports")
public class PortController {
    @Autowired
    PortService portService;
    
    @GetMapping("/device/{id}")
    public ResponseEntity<?> getPortsByDeviceId(@PathVariable Optional<Long> id){
        if(id.isPresent()){
            List<Port> list=portService.getPortByDeviceId(id.get());
            if(list.isEmpty()){
                return ResponseEntity.notFound().build();
            }else {
                return ResponseEntity.ok(list);
            }
        }else {
            return  ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }
}
