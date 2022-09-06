package com.filling.good.global.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthyController {

    // 무중단 배포를 위한 health check
    @GetMapping("/health")
    public String checkHealth() {
        return "건강한 FILLing GOOD !";
    }

}
