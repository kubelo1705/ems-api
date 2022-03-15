package com.example.managedevices.repository;

import com.example.managedevices.entity.NtpServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NtpServerRepository extends JpaRepository<NtpServer, Long> {
    NtpServer findNtpServerByState(boolean state);
    NtpServer findNtpServerById(Long id);
}
