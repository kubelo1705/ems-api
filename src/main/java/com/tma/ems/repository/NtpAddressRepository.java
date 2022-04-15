package com.tma.ems.repository;

import com.tma.ems.entity.Ntpaddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NtpAddressRepository extends JpaRepository<Ntpaddress, Long> {
    boolean existsByNtpserver_IdAndAddress(Long id, String address);

    List<Ntpaddress> findNtpaddressByAddress(String address);

    void deleteAllByNtpserver_Id(Long id);

    void deleteByAddress(String address);

}
