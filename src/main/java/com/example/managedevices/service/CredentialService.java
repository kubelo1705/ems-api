package com.example.managedevices.service;

import com.example.managedevices.entity.Credential;

import java.util.List;

public interface CredentialService {
    List<Credential> getAllCredentials();
    Credential saveCredential(Credential credential);
    boolean checkCredentialId(Long id);
    boolean isUsed(Long id);
}
