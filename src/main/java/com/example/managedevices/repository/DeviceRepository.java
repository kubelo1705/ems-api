package com.example.managedevices.repository;

import com.example.managedevices.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface DeviceRepository extends JpaRepository<Device,Long> {
    Device findDeviceById(Long id);
    List<Device> findDeviceByType(String type);
    Device findDeviceByIpAddress(String ipAddress);
    boolean existsByCredential_Id(Long id);
    void deleteDeviceById(Long id);
    boolean existsByIdAndStatus(Long id,boolean status);
    boolean existsByCredential_IdAndStatus(Long idCredential,boolean status);
    boolean existsByIpAddress(String ipAddress);
}
