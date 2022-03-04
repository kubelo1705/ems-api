package com.example.managedevices.service.impl;

import com.example.managedevices.entity.Credential;
import com.example.managedevices.repo.CredentialRepo;
import com.example.managedevices.repo.DeviceRepo;
import com.example.managedevices.service.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {
    private final CredentialRepo credentialRepo;
    private final DeviceRepo deviceRepo;
    @Override
    public List<Credential> getAllCredentials() {
        return credentialRepo.findAll();
    }

    @Override
    public Credential saveCredential(Credential credential) {
        return credentialRepo.save(credential);
    }

    @Override
    public boolean checkCredentialId(Long id) {
        return credentialRepo.findCredentialById(id)!=null?true:false;
    }

    @Override
    public boolean isUsed(Long id) {
        return deviceRepo.existsByCredential_Id(id);
    }
}
