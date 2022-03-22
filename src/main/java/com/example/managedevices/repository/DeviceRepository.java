package com.example.managedevices.repository;

import com.example.managedevices.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface DeviceRepository extends JpaRepository<Device,Long> {
    Device findDeviceById(Long id);
    List<Device> findDeviceByType(String type);
    Device findDeviceByIpAddress(String ipAddress);
    boolean existsByCredential_Id(Long id);
    void deleteDeviceById(Long id);
    boolean existsByIdAndConnected(Long id, boolean isConnected);
    boolean existsByCredential_IdAndConnected(Long idCredential, boolean isConnected);
    boolean existsByIpAddress(String ipAddress);

}
