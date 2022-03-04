package com.example.managedevices.repo;

import com.example.managedevices.entity.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortRepo extends JpaRepository<Port,Long> {
}
