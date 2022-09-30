package com.example.demo.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Data
public class AndroidDevice {
    @Id
    String macAddress;

    boolean isConnected;

    double currentWatt;

    @OneToOne
    @JoinColumn(name="arduino_mac_addr")
    Arduino arduino;
}
