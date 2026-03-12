package com.electrician.spark_e.controller;

import com.electrician.spark_e.repository.CustomerRepository;
import com.electrician.spark_e.repository.JobRepository;
import com.electrician.spark_e.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @GetMapping("/stats")
    public ResponseEntity<?> getDashboardStats() {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Calculate statistics from real database
            long totalJobs = jobRepository.count();
            long totalClients = customerRepository.count();
            long totalInvoices = invoiceRepository.count();
            
            // For now, use real counts and mock revenue
            long activeJobs = totalJobs; // All jobs count as active for demo
            long pendingInvoices = totalInvoices; // All invoices count as pending for demo
            double monthlyRevenue = 12500.0; // Keep mock revenue for now

            Map<String, Object> stats = Map.of(
                "activeJobs", activeJobs,
                "totalClients", totalClients,
                "pendingInvoices", pendingInvoices,
                "monthlyRevenue", monthlyRevenue
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load dashboard stats"));
        }
    }
}
