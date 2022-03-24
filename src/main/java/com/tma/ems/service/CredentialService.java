package com.tma.ems.service;

import com.tma.ems.entity.Credential;

import java.util.List;

public interface CredentialService {
    List<Credential> getAllCredentials();
    Credential addCredential(Credential credential);
    Credential updateCredential(Credential credential,Long id);
    boolean deleteCredential(Long id);
}
