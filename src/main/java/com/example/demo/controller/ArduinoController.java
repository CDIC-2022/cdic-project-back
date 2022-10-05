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
    public int getDailyWatt(@RequestBody HashMap<String, Object> map){
        String androidDeviceMac = (String)map.get("MAC");
        // 예외처리 꼭!
        Arduino arduino;
        try{
        arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double dailyWatt = arduino.getDailyWatt();
        System.out.printf("Day watt: %f",dailyWatt);
        return (int)dailyWatt;
    }

    @ResponseBody
    @RequestMapping(value = "/monthWatt", method = RequestMethod.POST)
    public int getMonthWatt(@RequestBody HashMap<String, Object> map){
        // 예외처리 꼭!
        String androidDeviceMac = (String)map.get("MAC");
        Arduino arduino;
        try{
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double monthWatt = arduino.getMonthWatt();
        System.out.printf("Month watt: %f",monthWatt);
        return (int)monthWatt;
    }

    @ResponseBody
    @RequestMapping(value = "/monthPayment", method = RequestMethod.POST)
    public int getMonthPayment(@RequestBody HashMap<String, Object> map){
        String androidDeviceMac = (String) map.get("MAC");
        // 예외처리 꼭!
        Arduino arduino;
        try{
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double monthPay = arduino.getMonthPay();
        System.out.printf("Month watt: %f",monthPay);
        return (int)monthPay;
    }

    @ResponseBody
    @RequestMapping(value = "/expectWatt", method = RequestMethod.POST)
    public int getExpectReduceW(@RequestBody HashMap<String, Object> map){
        String androidDeviceMac = (String) map.get("MAC");
        // 예외처리 꼭!
        Arduino arduino;
        try{
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch(NoSuchElementException e) {
            return -1;
        }
        double expectWatt = arduino.getExpectWatt();
        System.out.printf("Expect watt: %f",expectWatt);
        return (int)expectWatt;
    }

    @ResponseBody
    @RequestMapping(value = "/restoreOnOff", method = RequestMethod.POST)
    public int restoreOnOff(@RequestBody HashMap<String, Object> map){
        String androidDeviceMac = (String) map.get("MAC");
        Arduino arduino;

        try {
            arduino = searchArduinoByAndroidDevice(androidDeviceMac);
        }catch (NoSuchElementException e){
            return -1;
        }

        if(arduino.isConnected() == true){
            arduino.setConnected(false);
        }else {
            arduino.setConnected(true);
        }
        arduinoRepository.save(arduino);
        return 1;
    }

    @ResponseBody
    @RequestMapping(value = "/checkOnOff", method = RequestMethod.POST)
    public int checkOnOff(@RequestBody HashMap<String, Object> map){
        String androidDeviceMac = (String) map.get("MAC");
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
        Arduino arduino = arduinoRepository.findByMacAddress(arduinoMac).orElseThrow();
        // 실제로 5초마다 전송 받는 데이터가 daily watt는 아니지만 그냥 여기선 이걸로 초기화한다.
        double current = Double.parseDouble((String)map.get("current"));
        arduino.setDailyWatt(current);
        System.out.println(map);

        if(Integer.parseInt((String) map.get("isConnected")) == 1 && arduino.isConnected() == false){
            return Response.DISCONNECT.getValue();
        }else if(Integer.parseInt((String) map.get("isConnected")) == 0 && arduino.isConnected() == true){
            return Response.CONNECT.getValue();
        }
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
        double oneMinuteCurrent = Double.parseDouble((String)map.get("current"));

        //todo expected watt calculate
        updateWatt(arduino, calculateWatt(oneMinuteCurrent,1/60.0));
        arduinoRepository.save(arduino);
        return Response.NONE.getValue();
    }

    private void updateWatt(Arduino arduino, double oneMinuteWatt){
        updateDailyWatt(arduino,oneMinuteWatt);
        updateMonthlyWatt(arduino, oneMinuteWatt);
        updateExpectedWatt(arduino);
        updateMonthPay(arduino);

        if(checkDayIsOver(arduino)){
            resetArduinoForNextDay(arduino);
        }
        if(checkMonthIsOver(arduino)){
            resetArduinoForNextMonth(arduino);
        }
    }
    private double calculateWatt(double current, double timeHourUnit){
        return current*timeHourUnit;
    }

    private boolean checkDayIsOver(Arduino arduino){
        return arduino.getMinutesNum() >= 60*24;
    }
    private boolean checkMonthIsOver(Arduino arduino){
        return arduino.getDayNum()>=30;
    }

    private void resetArduinoForNextDay(Arduino arduino){
        arduino.setMinutesNum(0);
    }

    private void resetArduinoForNextMonth(Arduino arduino){
        arduino.setDayNum(0);
    }

    private void updateDailyWatt(Arduino arduino, double oneMinuteWatt){
        double dailyWatt = arduino.getDailyWatt();
        arduino.setDailyWatt(dailyWatt+oneMinuteWatt);
    }

    private void updateMonthlyWatt(Arduino arduino, double oneMinuteWatt){
        double monthlyWatt = arduino.getMonthWatt();
        arduino.setMonthWatt(monthlyWatt+oneMinuteWatt);
    }

    private void updateExpectedWatt(Arduino arduino){
        arduino.setExpectWatt(arduino.getMonthWatt()/ arduino.getDayNum()*30);
    }

    private void updateMonthPay(Arduino arduino){
        arduino.setMonthPay(arduino.getMonthWatt()*0.073);
    }

}
