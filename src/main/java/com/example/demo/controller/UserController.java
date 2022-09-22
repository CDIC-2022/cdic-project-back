package com.example.demo.controller;

import com.example.demo.Repository.UserRepository;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @ResponseBody
    @RequestMapping(value = "/register", method= RequestMethod.POST)
    public User Register(@RequestBody User user){
        System.out.println(user.getEmail() + " try to register");
        return userRepository.save(user);
    }

    @ResponseBody
    @RequestMapping(value ="/login", method=RequestMethod.POST)
    public User Login(@RequestBody User user){
        System.out.println(user.getEmail() + " try to login");
        System.out.println(user.getPassword());
        User isUser = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());

        if(isUser == null){
            System.out.println("Login failed");
            return null;
        }
        System.out.println(user.getEmail() + " loged in");
        return isUser;
    }

}
