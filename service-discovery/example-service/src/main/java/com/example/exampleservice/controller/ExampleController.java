package com.example.exampleservice.controller;

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleController {


    // Inject the server port from application properties
    @Value("${server.port}")
    private String serverPort;


    @GetMapping("/example")
    public String exampleEndpoint() {
       return String.format("Hello from Example Service on port %s!", serverPort);
    }
}