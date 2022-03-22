package com.example.managedevices.service.impl;

import com.example.managedevices.constant.Message;
import com.example.managedevices.entity.Credential;
import com.example.managedevices.exception.BadRequestException;
import com.example.managedevices.exception.ConflictException;
import com.example.managedevices.exception.NotFoundException;
import com.example.managedevices.repository.CredentialRepository;
import com.example.managedevices.repository.DeviceRepository;
import com.example.managedevices.service.CredentialService;
import com.example.managedevices.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * solve logic related to credential
 */
@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {
    private final CredentialRepository credentialRepo;
    private final DeviceRepository deviceRepo;

    /**
     * get all credentials from database
     * @return
     */
    @Override
    public List<Credential> getAllCredentials() {
        List<Credential> credentials=credentialRepo.findAll();
        if(credentials.isEmpty()){
            throw new NotFoundException(Message.NON_EXIST_CREDENTIAL);
        }
        return credentials;
    }

    /**
     * add credential to database
     * @param credential
     * @return
     */
    @Override
    public Credential addCredential(Credential credential) {
        if(ValidationUtils.isValidCredential(credential)){
            return credentialRepo.save(credential);
        }
        else{
            throw new BadRequestException(Message.INVALID_DATA);
        }
    }

    /**
     * update credential to database
     * @param credential
     * @param id
     * @return
     */
    @Override
    public Credential updateCredential(Credential credential, Long id) {
        if (credentialRepo.existsById(id)) {
            if(!deviceRepo.existsByCredential_IdAndConnected(id,true)) {
                Credential credentialUpdate = credentialRepo.findCredentialById(id);
                try {
                    credentialUpdate.setName(credential.getName());
                    credentialUpdate.setUsername(credential.getUsername());
                    credentialUpdate.setPassword(credential.getPassword());
                    return credentialRepo.save(credentialUpdate);
                }catch (Exception e){
                    throw new BadRequestException(Message.INVALID_DATA);
                }
            }else{
                throw new ConflictException(Message.CREDENTIAL_IS_USED);
            }
        }else{
            throw new NotFoundException(Message.NON_EXIST_CREDENTIAL);
        }
    }

    /**
     * delete credential from database.if it is used by a device, can't delete
     * @param id
     * @return
     */
    @Override
    public boolean deleteCredential(Long id) {
        if (credentialRepo.existsById(id)) {
            if(!deviceRepo.existsByCredential_IdAndConnected(id,true)) {
                credentialRepo.deleteById(id);
                return true;
            }else {
                throw new ConflictException(Message.CREDENTIAL_IS_USED);
            }
        }else {
            return false;
        }
    }
}
