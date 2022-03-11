package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.exception.DeviceException;
import com.example.managedevices.repo.CredentialRepo;
import com.example.managedevices.repo.DeviceRepo;
import com.example.managedevices.service.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public Credential addCredential(Credential credential) {
        if(credential.getUsername().contains(" ")||credential.getPassword().contains(" ")){
            throw new DeviceException(Message.INVALID_DATA);
        }
        else{
            return credentialRepo.save(credential);
        }
    }

    @Override
    public boolean checkCredentialId(Long id) {
        return credentialRepo.findCredentialById(id)!=null?true:false;
    }

    @Override
    public boolean isUsed(Long id) {
        return deviceRepo.existsByCredential_Id(id);
    }

    @Override
    public Credential updateCredential(Credential credential, Long id) {
        if (checkCredentialId(id)) {
            Credential credentialUpdate=credentialRepo.findCredentialById(id);

            credentialUpdate.setName(credential.getName());
            credentialUpdate.setUsername(credential.getUsername());
            credentialUpdate.setPassword(credential.getPassword());

            return credentialRepo.save(credentialUpdate);
        }else{
            throw new DeviceException(Message.NON_EXIST_CREDENTIAL);
        }
    }

    @Override
    public void deleteCredential(Long id) {
        if (checkCredentialId(id)) {
           credentialRepo.deleteById(id);
        }else {
            throw new DeviceException(Message.NON_EXIST_CREDENTIAL);
        }
    }
}
