package com.example.managedevices.repository;

import com.example.managedevices.entity.Ntpaddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NtpAddressRepository extends JpaRepository<Ntpaddress,Long> {
    boolean existsByNtpserver_IdAndAddress(Long id,String address);
    List<Ntpaddress> findNtpaddressByAddress(String address);
    void deleteAllByNtpserver_Id(Long id);
}
