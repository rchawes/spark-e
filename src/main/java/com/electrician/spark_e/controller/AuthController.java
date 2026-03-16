package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.User;
import com.electrician.spark_e.model.Electrician;
import com.electrician.spark_e.repository.UserRepository;
import com.electrician.spark_e.repository.ElectricianRepository;
import com.electrician.spark_e.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ElectricianRepository electricianRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        User fullUser = userRepository.findByUsername(user.getUsername()).orElse(null);
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "user", Map.of(
                "id", fullUser.getId(),
                "username", fullUser.getUsername(),
                "role", fullUser.getRole()
            )
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("ROLE_ELECTRICIAN");
        }
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of(
            "message", "User registered successfully",
            "user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
            )
        ));
    }
}
