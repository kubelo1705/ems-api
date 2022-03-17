package com.example.managedevices.repository;

import com.example.managedevices.entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepository extends JpaRepository<Port,Long> {
    void deleteAllByDevice_Id(Long id);
}
