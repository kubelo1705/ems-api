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

    @Override
    public List<Credential> getAllCredentials() {
        List<Credential> credentials = credentialRepo.findAll();
        return credentials;
    }

    @Override
    public Credential addCredential(Credential credential) {
        if (ValidationUtils.isValidCredential(credential)) {
            if (!credentialRepo.existsByName(credential.getName()))
                return credentialRepo.save(credential);
            else
                throw new BadRequestException(Message.DUPLICATE_CREDENTIAL);
        } else {
            throw new BadRequestException(Message.INVALID_DATA);
        }
    }


    @Override
    public Credential updateCredential(Credential newCredential, Long id) {
        if (credentialRepo.existsById(id)) {
            if (!deviceRepo.existsByCredential_Id(id)) {
                Credential credential = credentialRepo.findCredentialById(id);
                try {
                    mapNewCredentialToCredential(newCredential, credential);
                    return credentialRepo.save(credential);
                } catch (Exception e) {
                    throw new BadRequestException(Message.INVALID_DATA);
                }
            } else {
                throw new ConflictException(Message.CREDENTIAL_IS_USED);
            }
        } else {
            throw new NotFoundException(Message.NON_EXIST_CREDENTIAL);
        }
    }


    @Override
    public boolean deleteCredential(Long id) {
        if (credentialRepo.existsById(id)) {
            if (!deviceRepo.existsByCredential_Id(id)) {
                credentialRepo.deleteById(id);
                return true;
            } else {
                throw new ConflictException(Message.CREDENTIAL_IS_USED);
            }
        } else {
            return false;
        }
    }

    @Override
    public void mapNewCredentialToCredential(Credential newCredential, Credential credential) {
        if (newCredential.getName() != null) {
            credential.setName(newCredential.getName());
        }
        if (newCredential.getUsername() != null) {
            credential.setUsername(newCredential.getUsername());
        }
        if (newCredential.getPassword() != null) {
            credential.setPassword(newCredential.getPassword());
        }
    }
}
