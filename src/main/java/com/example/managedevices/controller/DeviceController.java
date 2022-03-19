package com.example.managedevices.controller;

import com.example.managedevices.constant.BaseCommand;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Device;
import com.example.managedevices.service.DeviceService;
import com.example.managedevices.utils.CommandUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/so/devices")
public class DeviceController {
    @Autowired
    DeviceService deviceService;

    @PostMapping("command/{id}")
    public ResponseEntity executeCommand(@PathVariable Optional<Long> id,@RequestBody Map<String,Object> command){
        if(id.isPresent()){
            try{
                return ResponseEntity.ok(deviceService.executeCommandByIdDevice(id.get(),command.get("command").toString()));
            }catch(Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else{
            return ResponseEntity.badRequest().body(Message.INVALID_DATA);
        }

    }
    @GetMapping("")
    public ResponseEntity<?> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        if (devices.isEmpty()) {
            return ResponseEntity.ok().body("Empty");
        }
        return ResponseEntity.ok(deviceService.getAllDevices());
    }

    @PostMapping("")
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
    @GetMapping("download/{id}")
    public ResponseEntity downloadFile(@PathVariable Optional<Long> id) {
        Device device=deviceService.getDeviceById(id.get());
        if(device!=null){
            try {
                CommandUtils.execute(device, device.getCredential(), BaseCommand.CONFIGURATION_EXPORT);
                File file = ResourceUtils.getFile(BaseCommand.FILE_PATH);
                byte[] data = FileUtils.readFileToByteArray(file);
                InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
                InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
                return ResponseEntity.ok(inputStreamResource);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else{
            return ResponseEntity.badRequest().body(Message.NON_EXIST_DEVICE);
        }
    }

    @GetMapping("load/{id}")
    public ResponseEntity reloadDevice(@PathVariable Optional<Long> id){
        Device device=deviceService.resync(deviceService.getDeviceById(id.get()));
        return ResponseEntity.ok(device);
    }
}
