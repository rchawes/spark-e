package com.electrician.spark_e.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Application is running!");
    }

    @GetMapping("/jobs-test")
    public ResponseEntity<String> jobsTest() {
        return ResponseEntity.ok("Jobs endpoint is accessible!");
    }
}
