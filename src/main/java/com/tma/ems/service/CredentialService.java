package com.tma.ems.service;

import com.tma.ems.entity.Credential;

import java.util.List;

/**
 * solve logic about credential
 */
public interface CredentialService {
    /**
     * get all credentials from database
     * @return
     */
    List<Credential> getAllCredentials();

    /**
     * add new credential to database and device
     * @param credential
     * @return
     */
    Credential addCredential(Credential credential);

    /**
     * update an existed interface in device and update to database
     * @param credential
     * @param id
     * @return
     */
    Credential updateCredential(Credential credential,Long id);

    /**
     * delete an existed interface in device and update to database
     * @param id
     * @return
     */
    boolean deleteCredential(Long id);

    /**
     * map new credential to old credential when update
     * @param newCredential
     * @param credential
     */
    void mapNewCredentialToCredential(Credential newCredential,Credential credential);
}
