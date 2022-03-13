package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Interface;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.service.InterfaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("api/v1/so/interfaces")
public class InterfaceController {
    InterfaceService interfaceService;

    @PostMapping("")
    public ResponseEntity<?> addInterface(@Valid @RequestBody Interface interfaceAdd) {
        try{
            return ResponseEntity.ok(interfaceService.addInterface(interfaceAdd));
        }catch (Exception e){
            throw new EmsException(e.getMessage());
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

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteInterface(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            try{
                interfaceService.deleteInterfaceById(id.get());
                return ResponseEntity.ok(Message.DELETE_SUCCESSFUL);
            }catch(Exception e){
                return ResponseEntity.badRequest().body(Message.NON_EXIST_INTERFACE);
            }
        }else{
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }
}
