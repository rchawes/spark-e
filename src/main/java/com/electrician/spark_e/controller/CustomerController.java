package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Customer;
import com.electrician.spark_e.repository.CustomerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced Customer Controller with pagination and validation
 */
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerRepository customerRepository;

    /**
     * Get all customers with pagination and sorting
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Fetching customers: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<Customer> customers = customerRepository.findAll(pageable);
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        log.info("Fetching customer with id: {}", id);
        
        return customerRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search customers by name or email
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Page<Customer>> searchCustomers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "lastName") String sortBy) {
        
        log.info("Searching customers with query: {}", query);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sortBy));
        Page<Customer> customers = customerRepository.searchCustomers(query, pageable);
        
        return ResponseEntity.ok(customers);
    }

    /**
     * Create new customer with validation
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
        log.info("Creating new customer: {}", customer.getEmail());
        
        try {
            // Check if email already exists
            if (customerRepository.existsByEmail(customer.getEmail())) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Email already exists");
                return ResponseEntity.badRequest().body(response);
            }
            
            Customer savedCustomer = customerRepository.save(customer);
            log.info("Customer created successfully with id: {}", savedCustomer.getId());
            
            return ResponseEntity.status(201).body(savedCustomer);
            
        } catch (Exception e) {
            log.error("Error creating customer", e);
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to create customer: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update existing customer
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id, 
            @Valid @RequestBody Customer customer) {
        
        log.info("Updating customer with id: {}", id);
        
        return customerRepository.findById(id)
            .map(existingCustomer -> {
                customer.setId(id);
                Customer updatedCustomer = customerRepository.save(customer);
                log.info("Customer updated successfully");
                return ResponseEntity.ok(updatedCustomer);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete customer
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        log.info("Deleting customer with id: {}", id);
        
        if (!customerRepository.existsById(id)) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Customer not found");
            return ResponseEntity.notFound().build();
        }
        
        customerRepository.deleteById(id);
        log.info("Customer deleted successfully");
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get customer statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> getCustomerStats() {
        log.info("Fetching customer statistics");
        
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countByIsActiveTrue();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", totalCustomers);
        stats.put("active", activeCustomers);
        stats.put("inactive", totalCustomers - activeCustomers);
        
        return ResponseEntity.ok(stats);
    }
}
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> GetCustomerById(@NonNull @PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Customer CreateCustomer(@NonNull @RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> UpdateCustomer(@NonNull @PathVariable Long id, @NonNull @RequestBody Customer customerDetails) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setFirstName(customerDetails.getFirstName());
                    customer.setLastName(customerDetails.getLastName());
                    customer.setEmail(customerDetails.getEmail());
                    customer.setPhone(customerDetails.getPhone());
                    customer.setAddress(customerDetails.getAddress());
                    return ResponseEntity.ok(customerRepository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> DeleteCustomer(@NonNull @PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
