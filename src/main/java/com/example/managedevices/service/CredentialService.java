package com.example.managedevices.service;

import com.example.managedevices.entity.Credential;

import java.util.List;

public interface CredentialService {
    List<Credential> getAllCredentials();
    Credential addCredential(Credential credential);
    boolean checkCredentialId(Long id);
    boolean isUsed(Long id);
    Credential updateCredential(Credential credential,Long id);
    void deleteCredential(Long id);
}
