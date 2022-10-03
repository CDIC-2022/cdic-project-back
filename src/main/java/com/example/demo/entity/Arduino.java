package com.example.demo.entity;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;


@Entity
@Data
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

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public double getDailyWatt() {
        return dailyWatt;
    }

    public void setDailyWatt(double dailyWatt) {
        this.dailyWatt = dailyWatt;
    }

    public double getMonthWatt() {
        return monthWatt;
    }

    public void setMonthWatt(double monthWatt) {
        this.monthWatt = monthWatt;
    }

    public double getMonthPay() {
        return monthPay;
    }

    public void setMonthPay(double monthPay) {
        this.monthPay = monthPay;
    }

    public double getExpectWatt() {
        return expectWatt;
    }

    public void setExpectWatt(double expectWatt) {
        this.expectWatt = expectWatt;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public AndroidDevice getAndroidDevice() {
        return androidDevice;
    }

    public void setAndroidDevice(AndroidDevice androidDevice) {
        this.androidDevice = androidDevice;
    }

    @OneToOne(mappedBy="arduino")
    AndroidDevice androidDevice;

}
