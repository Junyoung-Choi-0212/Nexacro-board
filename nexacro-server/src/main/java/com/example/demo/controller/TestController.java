package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // CORS 허용
public class TestController {

    @GetMapping("/hello")
    public String hello() {
    	System.out.println("Hello from Nexacro Server!");
        return "Hello from Nexacro Server!";
    }

    @PostMapping("/data")
    public String receiveData(@RequestBody String payload) {
        System.out.println("Received from Nexacro: " + payload);
        return "Server received: " + payload;
    }
}