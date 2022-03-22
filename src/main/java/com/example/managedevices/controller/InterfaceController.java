package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.service.InterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/interfaces")
public class InterfaceController {
    @Autowired
    InterfaceService interfaceService;

    @GetMapping("device/{id}")
    public ResponseEntity getInterfacesByDeviceId(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            List<Interface> list = interfaceService.getInterfacesByDeviceId(id.get());
            if (list.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(list);
            }
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addInterface(@RequestBody Map<String, Object> map) {
        if (map.get("id_device") != null && map.get("interface_name") != null && map.get("port_name") != null) {
            Long idDevice = Long.parseLong(map.get("id_device").toString());

            return ResponseEntity.ok(interfaceService.addInterface(idDevice, map));
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_DATA);
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateInterface(@RequestBody Map<String, Object> map) {
        if (map != null) {
            if (map.get("id_device") != null) {
                Long idDevice = Long.parseLong(map.get("id_device").toString());
                interfaceService.updateInterface(idDevice, map);
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        } else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteInterface(@RequestBody Map<String, Object> map) {
        if (!map.isEmpty()) {
            if (map.get("interface_name") != null && map.get("id_device") != null) {
                interfaceService.deleteInterface(Long.parseLong(map.get("id_device").toString()), map.get("interface_name").toString());
                return ResponseEntity.ok(Message.SUCCESSFUL);
            } else {
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
