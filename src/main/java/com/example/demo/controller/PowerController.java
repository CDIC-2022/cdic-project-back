package com.example.demo.controller;

import com.example.demo.Repository.DeviceRepository;
import com.example.demo.entity.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/power")
public class PowerController {
    @Autowired
    private DeviceRepository deviceRepository;

    @ResponseBody
    @RequestMapping(value = "/checkOnOff", method = RequestMethod.POST)
    public boolean powerOnOff(@RequestBody String deviceName){
        //arduino power on off check
        System.out.println(deviceName);
        return false;
    }

    @ResponseBody
    @RequestMapping(value = "/powerSaveOn", method = RequestMethod.POST)
    public boolean powerSaveOn(@RequestBody String deviceName){

        return true;
    }

    @ResponseBody

    @RequestMapping(value = "/powerSaveOff", method = RequestMethod.POST)
    public boolean powerSaveOff(@RequestBody String deviceName){

        return true;
    }

    @ResponseBody

    @RequestMapping(value = "/dayReduceW", method = RequestMethod.POST)
    public int dayReduceW(@RequestBody String deviceName){
        System.out.println("Day reduce power consumption");
        return 10;
    }

    @ResponseBody

    @RequestMapping(value = "/monthReduceW", method = RequestMethod.POST)
    public int monthReduceW(@RequestBody String deviceName){
        System.out.println("Month reduce power consumption");
        return 100;
    }

    @ResponseBody

    @RequestMapping(value = "/monthPayment", method = RequestMethod.POST)
    public int monthPayment(@RequestBody String deviceName){
        System.out.println("Month payment");
        return 120;
    }

    @ResponseBody
    @RequestMapping(value = "/expectReduceW", method = RequestMethod.POST)
    public int expectReduceW(@RequestBody String deviceName){
        System.out.println("Predict how much reduce power consumption");
        return 110;
    }

    @ResponseBody
    @RequestMapping(value = "/deviceOn", method = RequestMethod.POST)
    public Device deviceOn(@RequestBody String dummy){
        HttpServletRequest req = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        String ip = req.getHeader("X-FORWARDED-FOR");
        if (ip == null)
            ip = req.getRemoteAddr();
        Device device = new Device();
        device.setDeviceIP(ip);

        return deviceRepository.save(device);
    }
}
