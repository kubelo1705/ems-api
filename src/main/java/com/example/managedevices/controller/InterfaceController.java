package com.example.managedevices.controller;

import com.example.managedevices.constant.Command;
import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.service.InterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/so/interfaces")
public class InterfaceController {
    @Autowired
    InterfaceService interfaceService;

    @PostMapping("")
    public ResponseEntity<?> addInterface(@Valid @RequestBody Interface interfaceAdd) {
        try{
            interfaceService.addInterface(interfaceAdd);
            return ResponseEntity.ok(Message.SUCCESSFUL);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(Message.UNSUCCESSFUL);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateInterface(@Valid @RequestBody Interface interfaceUpdate, @PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            try {
                return ResponseEntity.ok(interfaceService.updateInterface(interfaceUpdate, id.get()));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else{
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deleteInterface(@RequestBody Map<String,Object> map) {
        if(!map.isEmpty()) {
            if (map.get("name") != null && map.get("idDevice")!=null) {
                try{
                    interfaceService.deleteInterface(Long.parseLong(map.get("id").toString()),map.get("name").toString());
                    return ResponseEntity.ok().build();
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
