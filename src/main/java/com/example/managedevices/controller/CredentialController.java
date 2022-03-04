package com.example.managedevices.controller;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.service.CredentialService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Repository
@RequestMapping("credential")
public class CredentialController {
    CredentialService credentialService;

    @GetMapping("")
    public ResponseEntity<?> getAllCredentials(){
        return ResponseEntity.ok(credentialService.getAllCredentials());
    }

    @PostMapping("add")
    public ResponseEntity<?> addCredential(@Valid @RequestBody Credential credential){
        return ResponseEntity.ok(credentialService.saveCredential(credential));
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateCredential(@PathVariable Optional<Long> id, @Valid @RequestBody Credential credential) {
        if (id.isPresent()) {
            if (credentialService.checkCredentialId(id.get())) {
                credential.setId(id.get());
                return ResponseEntity.ok(credentialService.saveCredential(credential));
            }
            return ResponseEntity.badRequest().body(Message.NON_EXIST_CREDENTIAL);
        }else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCredential(@PathVariable Optional<Long> id, @Valid @RequestBody Credential credential) {
        if (id.isPresent()) {
            if (credentialService.checkCredentialId(id.get())) {
                if(!credentialService.isUsed(id.get()))
                    return ResponseEntity.ok(credentialService.saveCredential(credential));
                else
                    return ResponseEntity.badRequest().body(Message.CREDENTIAL_IS_USED);
            }
            return ResponseEntity.badRequest().body(Message.NON_EXIST_CREDENTIAL);
        }else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }


}
