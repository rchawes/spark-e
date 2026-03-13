package com.electrician.spark_e.config;

import com.electrician.spark_e.security.JwtRequestFilter;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain filterChain(@Nonnull HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // <-- this disables CSRF
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/index.html", "/index-react.html", "/spark-e.html", "/spark-e-simple.html", "/debug-react.html", "/test.html", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/dashboard/**").permitAll()
                        .requestMatchers("/api/jobs/**", "/jobs/**").permitAll()
                        .requestMatchers("/api/customers/**", "/customers/**").permitAll()
                        .requestMatchers("/api/invoices/**", "/invoices/**").permitAll()
                        .requestMatchers("/api/electricians/**", "/electricians/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}