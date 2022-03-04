package com.example.managedevices.repo;

import com.example.managedevices.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Repository
public interface DeviceRepo extends JpaRepository<Device,Long> {
    Device findDeviceById(Long id);
    List<Device> findDeviceByType(String type);
    List<Device> findDevicesByIpAddressContains(String ipAddress);
    boolean existsByCredential_Id(Long id);
    void deleteDeviceById(Long id);
    boolean existsById(Long id);
}
