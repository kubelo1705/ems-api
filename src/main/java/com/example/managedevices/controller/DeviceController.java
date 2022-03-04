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
    public ResponseEntity<?> getAllCredentials() {
        List<Device> devices = deviceService.getAllDevices();
        if (devices.isEmpty()) {
            return ResponseEntity.ok().body("Empty");
        }
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @PostMapping("add")
    public ResponseEntity<?> addCredential(@Valid @RequestBody Device device) {
        if (deviceService.checkValidIpv4(device)) {
            return ResponseEntity.ok(deviceService.addDevice(device));
        }
        return ResponseEntity.badRequest().body(Message.INVALID_IP);
    }

    @GetMapping("/search/id/{id}")
    public ResponseEntity<?> getDeviceById(@PathVariable Optional<Long> id) {
        if(id.isPresent()) {
            Device device = deviceService.getDeviceById(id.get());
            if (device != null) {
                return ResponseEntity.ok(device);
            } else {
                return ResponseEntity.badRequest().body(Message.NON_EXIST_DEVICE);
            }
        }else{
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("search/ipaddress/{ipAddress}")
    public ResponseEntity<?> searchDeviceByIpaddress(@PathVariable Optional<String> ipAddress) {
        if(ipAddress.isPresent()) {
            List<Device> devices = deviceService.searchDeviceByIpaddress(ipAddress.get());
            if (devices.isEmpty()) {
                return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
            }
            return ResponseEntity.ok(devices);
        }else{
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("search/type/{type}")
    public ResponseEntity<?> searchDeviceByType(@PathVariable Optional<String> type) {
        if(type.isPresent()) {
            List<Device> devices = deviceService.getDevicesByType(type.get());
            if (devices.isEmpty()) {
                return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
            }
            return ResponseEntity.ok(devices);
        }else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Optional<Long> id){
        if(id.isPresent()) {
            if (deviceService.isValidId(id.get())) {
                deviceService.deleteDeviceById(id.get());
                return ResponseEntity.ok(Message.SUCCESSFUL);
            } else {
                return ResponseEntity.badRequest().body(Message.NON_EXIST_DEVICE);
            }
        }
        else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }
}
