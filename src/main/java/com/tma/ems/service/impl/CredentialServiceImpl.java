package com.tma.ems.service.impl;

import com.tma.ems.constant.Message;
import com.tma.ems.entity.Credential;
import com.tma.ems.exception.BadRequestException;
import com.tma.ems.exception.ConflictException;
import com.tma.ems.exception.NotFoundException;
import com.tma.ems.repository.CredentialRepository;
import com.tma.ems.repository.DeviceRepository;
import com.tma.ems.service.CredentialService;
import com.tma.ems.utils.ValidationUtils;
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
            if(!deviceRepo.existsByCredential_Id(id)) {
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
     * delete credential from database. if it is used by a device, can't delete
     * @param id
     * @return
     */
    @Override
    public boolean deleteCredential(Long id) {
        if (credentialRepo.existsById(id)) {
            if(!deviceRepo.existsByCredential_Id(id)) {
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
