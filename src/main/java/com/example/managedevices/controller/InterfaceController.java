package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.service.InterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/so/interfaces")
public class InterfaceController {
    @Autowired
    InterfaceService interfaceService;

    @PostMapping("")
    public ResponseEntity<?> addInterface(@RequestBody Map<String,Object> map) {
        if(map.get("idDevice")!=null && map.get("interface")!=null) {
            try {
                Long idDevice=Long.parseLong(map.get("idDevice").toString());
                Interface interfaceAdd=(Interface) map.get("interface");
                interfaceService.addInterface(interfaceAdd,idDevice);
                return ResponseEntity.ok(Message.SUCCESSFUL);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else {
            return ResponseEntity.badRequest().body(Message.INVALID_DATA);
        }
    }

    @PutMapping()
    public ResponseEntity<?> updateInterface( @RequestBody Map<String,Object> map) {
        if (map!=null){
            if(map.get("idDevice")!=null && map.get("interface")!=null){
                try {
                    Long idDevice = Long.parseLong(map.get("idDevice").toString());
                    String interfaceName=map.get("interfaceName").toString();
                    Interface inf = (Interface) map.get("interface");
                    return ResponseEntity.ok(interfaceService.updateInterface(interfaceName,inf,idDevice));
                }catch (Exception e){
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }else {
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        }else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteInterface(@RequestBody Map<String,Object> map) {
        if(!map.isEmpty()) {
            if (map.get("name") != null && map.get("idDevice")!=null) {
                try{
                    interfaceService.deleteInterface(Long.parseLong(map.get("idDevice").toString()),map.get("name").toString());
                    return ResponseEntity.ok(Message.SUCCESSFUL);
                }catch (Exception e){
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }else{
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        }else{
            return ResponseEntity.notFound().build();
        }
    }
}
