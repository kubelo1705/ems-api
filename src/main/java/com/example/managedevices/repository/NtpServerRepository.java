package com.example.managedevices.repository;

import com.example.managedevices.entity.Ntpserver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NtpServerRepository extends JpaRepository<Ntpserver, Long> {
    //Ntpserver findNtpServerByState(boolean state);
    Ntpserver findNtpServerById(Long id);
    void deleteAllByDevice_Id(Long id);
}
