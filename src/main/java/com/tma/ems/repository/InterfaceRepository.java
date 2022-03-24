package com.tma.ems.repository;

import com.tma.ems.entity.Interface;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterfaceRepository extends JpaRepository<Interface,Long> {
    List<Interface> findInterfaceByDevice_Id(Long id);
    void deleteById(Long id);
    void deleteAllByDevice_Id(Long id);
    boolean existsByNameAndDevice_Id(String name,Long idDevice);
    void deleteByNameAndDevice_Id(String name,Long idDevice);
    Interface findInterfaceByNameAndDevice_Id(String name,Long idDevice);
}
