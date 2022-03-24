package com.tma.ems.controller;

import com.tma.ems.constant.Message;
import com.tma.ems.entity.Ntpaddress;
import com.tma.ems.service.NtpServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/ntpservers")
@Transactional
public class NtpServerController {
    @Autowired
    NtpServerService ntpServerService;

    @GetMapping("device/{id}")
    public ResponseEntity getNtpsevrerByDeviceId(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            return ResponseEntity.ok(ntpServerService.getNtpserverByDeviceId(id.get()));
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @PostMapping()
    public ResponseEntity addNtpserver(@RequestBody Map<String, Object> map) {
        if (!map.isEmpty()) {
            if (map.get("idDevice") != null && map.get("address") != null) {
                Long idDevice = Long.parseLong(map.get("idDevice").toString());
                String address = map.get("address").toString();
                return ResponseEntity.ok(ntpServerService.addNtpserver(idDevice, new Ntpaddress(address)));
            } else {
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        } else {
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
    public ResponseEntity deleteNtpserver(@RequestBody Map<String, Object> map) {
        if (!map.isEmpty()) {
            if (map.get("address") != null && map.get("idDevice") != null) {
                ntpServerService.deleteNtpserver(Long.parseLong(map.get("idDevice").toString()), map.get("address").toString());
                return ResponseEntity.ok(Message.SUCCESSFUL);
            } else {
                return ResponseEntity.badRequest().body(Message.INVALID_DATA);
            }
        } else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }
}
