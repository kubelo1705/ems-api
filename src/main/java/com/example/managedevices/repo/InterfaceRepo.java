package com.example.managedevices.repo;

import com.example.managedevices.entity.Device;
import com.example.managedevices.entity.Interface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterfaceRepo extends JpaRepository<Interface,Long> {
    List<Interface> findInterfaceByDevice_Id(Long id);
    Interface findInterfaceById(Long id);
    void deleteById(Long id);
}
