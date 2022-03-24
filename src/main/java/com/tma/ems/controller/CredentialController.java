package com.tma.ems.controller;

import com.tma.ems.constant.Message;
import com.tma.ems.entity.Credential;
import com.tma.ems.service.CredentialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/credentials")
public class CredentialController {
    @Autowired
    CredentialService credentialService;

    @GetMapping("")
    public ResponseEntity<?> getAllCredentials() {
        return ResponseEntity.ok(credentialService.getAllCredentials());
    }

    @PostMapping("")
    public ResponseEntity<?> addCredential(@RequestBody Credential credential) {
        if (credential != null) {
            return ResponseEntity.ok(credentialService.addCredential(credential));
        } else {
            return ResponseEntity.badRequest().body(Message.INVALID_REQUEST);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<?> updateCredential(@PathVariable Optional<Long> id, @RequestBody Credential credential) {
        if (id.isPresent()) {
            return ResponseEntity.ok(credentialService.updateCredential(credential, id.get()));
        } else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCredential(@PathVariable Optional<Long> id) {
        if (id.isPresent()) {
            if (credentialService.deleteCredential(id.get())) {
                return ResponseEntity.ok(Message.SUCCESSFUL);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.badRequest().body(Message.EMPTY_INPUT_VALUE);
        }
    }
}
