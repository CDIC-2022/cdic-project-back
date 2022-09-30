package com.example.demo.Repository;

import com.example.demo.entity.Arduino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArduinoRepository extends JpaRepository<Arduino, Integer> {
    Optional<Arduino> findByMacAddress(String macAddress);

}
