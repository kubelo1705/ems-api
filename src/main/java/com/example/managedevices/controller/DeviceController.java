package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("device")
public class DeviceController {
    DeviceService deviceService;

    @GetMapping("")
    public ResponseEntity<?> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        if (devices.isEmpty()) {
            return ResponseEntity.ok().body("Empty");
        }
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @PostMapping("add")
    public ResponseEntity<?> addDevice(@Valid @RequestBody Device device) {
        try{
            return ResponseEntity.ok(deviceService.addDevice(device));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @GetMapping("/search/id/{id}")
    public ResponseEntity<?> getDeviceById(@PathVariable Optional<Long> id) {
        if(id.isPresent()) {
            try{
                return ResponseEntity.ok(deviceService.getDeviceById(id.get()));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else{
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("search/ipaddress/{ipAddress}")
    public ResponseEntity<?> searchDeviceByIpaddress(@PathVariable Optional<String> ipAddress) {
        if(ipAddress.isPresent()) {
            try{
                return ResponseEntity.ok(deviceService.getDeviceByIpaddress(ipAddress.get()));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else{
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("search/type/{type}")
    public ResponseEntity<?> searchDeviceByType(@PathVariable Optional<String> type) {
        if(type.isPresent()) {
            try{
                return ResponseEntity.ok(deviceService.getDevicesByType(type.get()));
            }catch(Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Optional<Long> id){
        if(id.isPresent()) {
            try {
                deviceService.deleteDeviceById(id.get());
                return ResponseEntity.ok(Message.DELETE_SUCCESSFUL);
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }
}
