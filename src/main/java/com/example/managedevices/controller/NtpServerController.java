package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.NtpServer;
import com.example.managedevices.service.NtpServerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/v1/so/ntpservers")
public class NtpServerController {
    NtpServerService ntpServerService;

    @GetMapping()
    public ResponseEntity getAllNtpservers(){
        return ResponseEntity.ok(ntpServerService.getAllNtpservers());
    }

    @PostMapping()
    public ResponseEntity addNtpserver(NtpServer ntpServer){
        try{
            return ResponseEntity.ok(ntpServerService.addNtpserver(ntpServer));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity updateNtpserver(NtpServer ntpServer, @PathVariable Long id){
        try{
            return ResponseEntity.ok(ntpServerService.updateNtpserver(ntpServer,id));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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
