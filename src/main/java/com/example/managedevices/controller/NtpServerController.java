package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Ntpaddress;
import com.example.managedevices.entity.Ntpserver;
import com.example.managedevices.service.NtpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/so/ntpservers")
@Transactional
public class NtpServerController {
    @Autowired
    NtpServerService ntpServerService;

    @GetMapping()
    public ResponseEntity getAllNtpservers(){
        return ResponseEntity.ok(ntpServerService.getAllNtpservers());
    }

    @PostMapping()
    public ResponseEntity addNtpserver(@RequestBody Map<String,Object> map){
        if(map!=null){
            if(map.get("idDevice")!=null && map.get("address")!=null){
                try{
                    Long idDevice=Long.parseLong(map.get("idDevice").toString());
                    String address=map.get("address").toString();
                    return ResponseEntity.ok(ntpServerService.addNtpserver(idDevice,new Ntpaddress(address)));
                }catch (Exception e){
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            }else {
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        }else{
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
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

    @DeleteMapping()
    public ResponseEntity deleteNtpserver(@RequestBody Map<String,Object> map){
        if(map!=null) {
            if (map.get("address") != null && map.get("idDevice") != null) {
                try {
                    ntpServerService.deleteNtpserver(Long.parseLong(map.get("idDevice").toString()), map.get("address").toString());
                    return ResponseEntity.ok(Message.SUCCESSFUL);
                } catch (Exception e) {
                    return ResponseEntity.badRequest().body(e.getMessage());
                }
            } else {
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        }else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }
}
