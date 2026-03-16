package com.agriconnect.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/")
    public String home() {
        return "AgriConnect Backend is running!";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}