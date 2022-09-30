package com.example.demo.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Arduino {

    @Id
    String macAddress;

    double dailyWatt;
    double monthWatt;
    double monthPay;
    double expectWatt;
    boolean isConnected;


    @OneToOne(mappedBy="arduino")
    AndroidDevice androidDevice;

}
