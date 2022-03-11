package com.example.managedevices.repo;

import com.example.managedevices.entity.NtpServer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NtpServerRepo extends JpaRepository<NtpServer, Long> {
    NtpServer findNtpServerByState(boolean state);
    NtpServer findNtpServerById(Long id);
}
