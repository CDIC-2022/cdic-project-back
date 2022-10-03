package com.example.demo.controller;

import com.example.demo.Repository.AndroidDeviceRepository;
import com.example.demo.Repository.ArduinoRepository;
import com.example.demo.data.Response;
import com.example.demo.entity.Arduino;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/arduino")
public class ArduinoController {
    @Autowired
    private AndroidDeviceRepository androidDeviceRepository;
    @Autowired
    private ArduinoRepository arduinoRepository;


    Arduino searchArduinoByAndroidDevice(String androidDeviceMac){
        return androidDeviceRepository.findByMacAddress(androidDeviceMac).orElseThrow().getArduino();
    }

    @ResponseBody
    @RequestMapping(value = "/dayWatt", method = RequestMethod.POST)
    public double getDailyWatt(@RequestBody String androidDeviceMac){
        // 예외처리 꼭!
        Arduino arduino;
        try{
        arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double dailyWatt = arduino.getDailyWatt();
        System.out.printf("Day watt: %f",dailyWatt);
        return dailyWatt;
    }

    @ResponseBody
    @RequestMapping(value = "/monthWatt", method = RequestMethod.POST)
    public double getMonthWatt(@RequestBody String androidDeviceMac){
        // 예외처리 꼭!
        Arduino arduino;
        try{
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double monthWatt = arduino.getMonthWatt();
        System.out.printf("Month watt: %f",monthWatt);
        return monthWatt;
    }

    @ResponseBody
    @RequestMapping(value = "/monthPayment", method = RequestMethod.POST)
    public double getMonthPayment(@RequestBody String androidDeviceMac){
        // 예외처리 꼭!
        Arduino arduino;
        try{
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double monthPay = arduino.getMonthPay();
        System.out.printf("Month watt: %f",monthPay);
        return monthPay;
    }

    @ResponseBody
    @RequestMapping(value = "/expectWatt", method = RequestMethod.POST)
    public double getExpectReduceW(@RequestBody String androidDeviceMac){
        // 예외처리 꼭!
        Arduino arduino;
        try{
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double expectWatt = arduino.getExpectWatt();
        System.out.printf("Expect watt: %f",expectWatt);
        return expectWatt;
    }

    @ResponseBody
    @RequestMapping(value = "/restoreOnOff", method = RequestMethod.POST)
    public int restoreOnOff(@RequestBody String androidDeviceMac){
        Arduino arduino;

        try {
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch (NoSuchElementException e){
            return -1;
        }

        if(arduino.isConnected() == true){
            arduino.setConnected(false);
        }else arduino.setConnected(true);

        return 1;
    }

    @ResponseBody
    @RequestMapping(value = "/checkOnOff", method = RequestMethod.POST)
    public int checkOnOff(@RequestBody String androidDeviceMac){
        System.out.println(androidDeviceMac);
        Arduino arduino;
        try {
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch (NoSuchElementException e){
            return -1;
        }

        if(arduino.isConnected()){
            return 1;
        }else return 0;
    }
    // 아두이노로부터 5초마다 전력 소비량 받기.
    // 아두이노 정보 등록되어 있지 않으면 DB에 새로 생성.
    @ResponseBody
    @RequestMapping(value = "/receiveCondition", method = RequestMethod.POST)
    public int receiveCondition(@RequestBody HashMap<String, Object> map){

        String arduinoMac = (String) map.get("MAC");
        System.out.println(arduinoMac);
        Arduino arduino = arduinoRepository.findByMacAddress(arduinoMac).orElseThrow();
        // 실제로 5초마다 전송 받는 데이터가 daily watt는 아니지만 그냥 여기선 이걸로 초기화한다.
        double current = Double.parseDouble((String)map.get("current"));
        arduino.setDailyWatt(current);

        if(Boolean.parseBoolean((String) map.get("isConnected")) == true && arduino.isConnected() == false){
            arduino.setConnected(false);
            arduinoRepository.save(arduino);
            return Response.DISCONNECT.getValue();
        }else if(Boolean.parseBoolean((String) map.get("isConnected")) == false && arduino.isConnected() == true){
            arduino.setConnected(true);
            arduinoRepository.save(arduino);
            return Response.CONNECT.getValue();
        }
        arduinoRepository.save(arduino);
        return Response.NONE.getValue();
    }

    // 60초마다 평균 전력 소비량 받기
    @ResponseBody
    @RequestMapping(value="/receiveAverageCurrent", method = RequestMethod.POST)
    public int receiveAverageCurrent(@RequestBody HashMap<String, Object> map){
        HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String arduinoIP = req.getHeader("X-FORWARDED-FOR");
        String arduinoMac = (String) map.get("MAC");
        Arduino arduino = arduinoRepository.findByMacAddress(arduinoMac).orElseThrow();
        arduino.setDailyWatt(arduino.getDailyWatt() + (double)map.get("current"));
        arduino.setMonthWatt(arduino.getMonthWatt() + (double)map.get("current"));

        //todo expected watt calculate
        //set as 0 (for test)
        arduino.setExpectWatt(0);

        if((boolean) map.get("isConnected") == true && arduino.isConnected() == false){
            arduino.setConnected(false);
            arduinoRepository.save(arduino);
            return Response.DISCONNECT.getValue();
        }else if((boolean) map.get("isConnected") == false && arduino.isConnected() == true){
            arduino.setConnected(true);
            arduinoRepository.save(arduino);
            return Response.CONNECT.getValue();
        }
        arduinoRepository.save(arduino);
        return Response.NONE.getValue();

    }

}
