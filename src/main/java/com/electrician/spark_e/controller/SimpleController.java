package com.electrician.spark_e.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/simple")
public class SimpleController {

    @PostMapping("/test-job")
    public ResponseEntity<String> testJobCreation(@RequestBody String jobData) {
        System.out.println("Received job data: " + jobData);
        return ResponseEntity.ok("Job creation test successful! Received: " + jobData);
    }

    @GetMapping("/test-auth")
    public ResponseEntity<String> testAuth() {
        return ResponseEntity.ok("Simple auth test endpoint working!");
    }
}
