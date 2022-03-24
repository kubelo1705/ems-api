package com.tma.ems.controller;

import com.tma.ems.constant.Command;
import com.tma.ems.constant.Message;
import com.tma.ems.entity.Device;
import com.tma.ems.exception.NotFoundException;
import com.tma.ems.service.DeviceService;
import com.tma.ems.utils.SshUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/devices")
public class DeviceController {
    @Autowired
    DeviceService deviceService;

    @PostMapping("command/{id}")
    public ResponseEntity executeCommand(@PathVariable Optional<Long> id, @RequestBody Map<String, Object> command) {
        if (id.isPresent()) {
            try {
                return ResponseEntity.ok(deviceService.executeCommandByIdDevice(id.get(), command.get("command").toString()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_DATA);
        }

    }

    @GetMapping("")
    public ResponseEntity<?> getAllDevices() {
        List<Device> devices = deviceService.getAllDevices();
        if (devices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(devices);
    }

    @PostMapping("")
    public ResponseEntity<?> addDevice(@RequestBody Device device) {
        if (device != null) {
            Device deviceAdd = deviceService.addDevice(device);
            if (deviceAdd != null) {
                return ResponseEntity.ok(deviceAdd);
            } else {
                return ResponseEntity.accepted().build();
            }
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("/search/id/{id}")
    public ResponseEntity<?> getDeviceById(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            return ResponseEntity.ok(deviceService.getDeviceById(id.get()));
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("search/ipaddress/{ipAddress}")
    public ResponseEntity<?> searchDeviceByIpaddress(@PathVariable Optional<String> ipAddress) {
        if (ipAddress.isPresent()) {
            return ResponseEntity.ok(deviceService.getDeviceByIpaddress(ipAddress.get()));
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("search/type/{type}")
    public ResponseEntity<?> searchDeviceByType(@PathVariable Optional<String> type) {
        if (type.isPresent()) {
            List<Device> devices = deviceService.getDevicesByType(type.get());
            return ResponseEntity.ok(devices);
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteDevice(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            deviceService.deleteDeviceById(id.get());
            return ResponseEntity.ok(Message.SUCCESSFUL);
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @GetMapping("download/{id}")
    public ResponseEntity downloadFile(@PathVariable Optional<Long> id) {
        Device device = deviceService.getDeviceById(id.get());
        if (device != null) {
            try {
                SshUtils.executeCommand(device, Command.CONFIGURATION_EXPORT);
                File file = ResourceUtils.getFile(Command.FILE_PATH);
                byte[] data = FileUtils.readFileToByteArray(file);
                InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(data));
                InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
                return ResponseEntity.ok(inputStreamResource);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        } else {
            return ResponseEntity.badRequest().body(Message.NON_EXIST_DEVICE);
        }
    }

    @GetMapping("load/{id}")
    public ResponseEntity reloadDevice(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            Device device = deviceService.getDeviceById(id.get());
            deviceService.resync(device);
            return ResponseEntity.ok(device);
        } else {
            throw new NotFoundException(Message.NON_EXIST_DEVICE);
        }
    }
}
