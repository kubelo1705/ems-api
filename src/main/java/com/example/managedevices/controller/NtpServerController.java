package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.service.NtpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/so/ntpservers")
public class NtpServerController {
    @Autowired
    NtpServerService ntpServerService;

    @GetMapping()
    public ResponseEntity getAllNtpservers(){
        return ResponseEntity.ok(ntpServerService.getAllNtpservers());
    }

    @PostMapping()
    public ResponseEntity addNtpserver(Ntpserver ntpServer){
        try{
            return ResponseEntity.ok(ntpServerService.addNtpserver(ntpServer));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PutMapping("{id}")
//    public ResponseEntity updateNtpserver(Ntpserver ntpServer, @PathVariable Long id){
//        try{
//            return ResponseEntity.ok(ntpServerService.updateNtpserver(ntpServer,id));
//        }catch (Exception e){
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    @DeleteMapping("{id}")
    public ResponseEntity deleteNtpserver(@PathVariable Long id){
        try{
            ntpServerService.deleteNtpserver(id);
            return ResponseEntity.ok(Message.DELETE_SUCCESSFUL);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
