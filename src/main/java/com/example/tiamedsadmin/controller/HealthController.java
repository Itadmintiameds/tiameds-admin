package com.example.tiamedsadmin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String healthCheck() {
        return "Server is healthy!";
    }

    @GetMapping("/health-check")
    public String healthCheckAlias() {
        return "Server is healthy!";
    }
}
