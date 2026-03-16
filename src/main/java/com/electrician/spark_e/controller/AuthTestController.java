package com.electrician.spark_e.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth-test")
public class AuthTestController {

    @PostMapping("/test-job")
    public ResponseEntity<String> testJobCreation(@RequestBody String jobData) {
        return ResponseEntity.ok("Job creation test successful! Received: " + jobData);
    }

    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Auth test endpoint working!");
    }
}
