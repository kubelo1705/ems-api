package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.service.InterfaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("interface")
public class InterfaceController {
    InterfaceService interfaceService;

    @PostMapping("add")
    public ResponseEntity<?> addInterface(@Valid @RequestBody Interface interfaceAdd) {
        if(!interfaceService.CheckInterfaceId(interfaceAdd.getId())) {
            try{
                if(interfaceService.checkValidInterface(interfaceAdd)){
                    return ResponseEntity.ok(interfaceService.addInterface(interfaceAdd));
                }
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body(Message.DUPLICATE_ID);
    }

    @PutMapping(value = {"{id}"})
    public ResponseEntity<?> updateInterface(@Valid @RequestBody Interface interfaceUpdate, @PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            if (interfaceService.CheckInterfaceId(id.get())) {
                try {
                    if (interfaceService.checkValidInterface(interfaceUpdate)) {
                        return ResponseEntity.ok(interfaceService.addInterface(interfaceUpdate));
                    }
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }
            return ResponseEntity.badRequest().body(Message.NON_EXIST_INTERFACE);
        }else{
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteInterface(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            if (interfaceService.CheckInterfaceId(id.get())) {
                interfaceService.deleteById(id.get());
                return ResponseEntity.ok(Message.SUCCESSFUL);
            }
            else{
                return ResponseEntity.badRequest().body(Message.NON_EXIST_INTERFACE);
            }
        }else{
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }
}
