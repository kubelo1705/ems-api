package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("api/v1/so/credentials")
public class CredentialController {
    @Autowired
    CredentialService credentialService;

    @GetMapping("")
    public ResponseEntity<?> getAllCredentials(){
        return ResponseEntity.ok(credentialService.getAllCredentials());
    }

    @PostMapping("")
    public ResponseEntity<?> addCredential(@Valid @RequestBody Credential credential){
        try{
            return ResponseEntity.ok(credentialService.addCredential(credential));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateCredential(@PathVariable Optional<Long> id, @Valid @RequestBody Credential credential) {
        if (id.isPresent()) {
            try{
                return ResponseEntity.ok(credentialService.updateCredential(credential,id.get()));
            }catch (Exception e){
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCredential(@PathVariable Optional<Long> id, @Valid @RequestBody Credential credential) {
        if (id.isPresent()) {
            credentialService.deleteCredential(id.get());
            return ResponseEntity.ok(Message.DELETE_SUCCESSFUL);
        }else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }
}
