package com.tma.ems.repository;

import com.tma.ems.entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortRepository extends JpaRepository<Port, Long> {
    void deleteAllByDevice_Id(Long id);

    List<Port> findPortsByDevice_Id(Long id);

    Port findPortByPortNameAndDevice_Id(String name, Long id);

}
