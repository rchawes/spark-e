package com.electrician.spark_e.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Enhanced Security Configuration with comprehensive protection
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class EnhancedSecurityConfig {

    private final JwtService jwtService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UsernamePasswordAuthenticationFilter authenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                String origin = request.getHeader("Origin");
                
                // Allow specific origins in production
                if (isAllowedOrigin(origin)) {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(Arrays.asList("/**"));
                    config.setAllowedOrigins(Arrays.asList(origin));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(Arrays.asList(
                        "Authorization",
                        "Content-Type",
                        "X-Requested-With",
                        "Accept",
                        "Origin",
                        "Access-Control-Request-Method",
                        "Access-Control-Request-Headers"
                    ));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }
                
                // Default configuration for non-allowed origins
                CorsConfiguration defaultConfig = new CorsConfiguration();
                defaultConfig.setAllowedOriginPatterns(Arrays.asList("/api/**"));
                defaultConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                defaultConfig.setAllowedHeaders(Arrays.asList("*"));
                defaultConfig.setAllowCredentials(false);
                return defaultConfig;
            }
        };
    }

    private boolean isAllowedOrigin(String origin) {
        List<String> allowedOrigins = Arrays.asList(
            "http://localhost:3000",
            "http://localhost:8082",
            "https://spark-e.onrender.com",
            "https://your-production-domain.com"
        );
        
        return origin != null && allowedOrigins.contains(origin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Configuring enhanced security filter chain");
        
        http
            // Disable CSRF for API
            .csrf(csrf -> csrf.disable())
            
            // Configure session management
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Configure authorization
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/health").permitAll()
                .requestMatchers("/api/ready").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                
                // Static resources
                .requestMatchers("/spark-e.html").permitAll()
                .requestMatchers("/static/**").permitAll()
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                
                // Protected API endpoints
                .requestMatchers("/api/customers/**").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers("/api/jobs/**").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers("/api/invoices/**").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers("/api/electricians/**").hasRole("ROLE_ADMIN")
                .requestMatchers("/api/project-pictures/**").hasAnyRole("ROLE_USER", "ROLE_ADMIN")
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            
            // Add security filter
            .addFilterBefore(authenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            
            // Configure headers
            .headers(headers -> headers
                .contentSecurityPolicy("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline'; img-src 'self' data: https:; font-src 'self'; connect-src 'self'; frame-ancestors 'none';")
                .xssProtection(xss -> xss
                    .header("X-Content-Type-Options", "nosniff")
                    .header("X-Frame-Options", "DENY")
                    .header("X-XSS-Protection", "1; mode=block")
                )
                .frameOptions(frameOptions -> frameOptions
                    .deny()
                )
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
                .referrerPolicy(referrer -> referrer.strictOriginWhenCrossOrigin())
            );
        
        return http.build();
    }
}
