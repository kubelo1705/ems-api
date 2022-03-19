package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.exception.EmsException;
import com.example.managedevices.repository.CredentialRepository;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.service.CredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {
    private final CredentialRepository credentialRepo;
    private final DeviceRepository deviceRepo;
    @Override
    public List<Credential> getAllCredentials() {
        return credentialRepo.findAll();
    }

    @Override
    public Credential addCredential(Credential credential) {
        if(credential.getUsername().contains(" ")||credential.getPassword().contains(" ")){
            throw new EmsException(Message.INVALID_DATA);
        }
        else{
            return credentialRepo.save(credential);
        }
    }

    @Override
    public Credential updateCredential(Credential credential, Long id) {
        if (credentialRepo.existsById(id)) {
            if(!deviceRepo.existsByCredential_IdAndStatus(id,true)) {
                Credential credentialUpdate = credentialRepo.findCredentialById(id);
                try {
                    credentialUpdate.setName(credential.getName());
                    credentialUpdate.setUsername(credential.getUsername());
                    credentialUpdate.setPassword(credential.getPassword());
                    return credentialRepo.save(credentialUpdate);
                }catch (Exception e){
                    throw new EmsException(Message.INVALID_DATA);
                }
            }else{
                throw new EmsException(Message.CREDENTIAL_IS_USED);
            }
        }else{
            throw new EmsException(Message.NON_EXIST_CREDENTIAL);
        }
    }

    @Override
    public void deleteCredential(Long id) {
        if (credentialRepo.existsById(id)) {
            if(!deviceRepo.existsByCredential_IdAndStatus(id,true)) {
                credentialRepo.deleteById(id);
            }else {
                throw new EmsException(Message.CREDENTIAL_IS_USED);
            }
        }else {
            throw new EmsException(Message.NON_EXIST_CREDENTIAL);
        }
    }
}
