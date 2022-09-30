package com.example.demo.Repository;
import com.example.demo.entity.AndroidDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.Optional;

public interface AndroidDeviceRepository extends JpaRepository<AndroidDevice, Integer>{

    Optional<AndroidDevice> findByMacAddress(String macAddress);

}
