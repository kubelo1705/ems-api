package com.tma.ems.repository;

import com.tma.ems.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Credential findCredentialById(Long id);

    boolean existsByName(String name);
}
