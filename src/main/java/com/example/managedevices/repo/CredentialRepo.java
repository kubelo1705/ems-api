package com.example.managedevices.repo;

import com.example.managedevices.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepo extends JpaRepository<Credential,Long> {
    Credential findCredentialById(Long id);
}
