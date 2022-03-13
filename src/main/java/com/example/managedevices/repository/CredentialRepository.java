package com.example.managedevices.repository;

import com.example.managedevices.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Credential,Long> {
    Credential findCredentialById(Long id);
}
