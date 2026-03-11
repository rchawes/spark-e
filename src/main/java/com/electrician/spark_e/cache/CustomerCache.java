package com.electrician.spark_e.cache;

import com.electrician.spark_e.model.Customer;
import com.electrician.spark_e.repository.CustomerRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@CacheConfig(cacheNames = "customers")
public class CustomerCache {

    private final CustomerRepository customerRepository;

    public CustomerCache(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // ========== READ OPERATIONS ==========

    /**
     * Find customer by ID - cached
     */
    @Cacheable(key = "#id", unless = "#result == null")
    public Optional<Customer> findById(@NonNull Long id) {
        return customerRepository.findById(id);
    }

    /**
     * Find customer by email - cached with email as key
     */
    @Cacheable(key = "'email:' + #email", unless = "#result == null")
    public Optional<Customer> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    /**
     * Get all customers - cached separately
     */
    @Cacheable(value = "allCustomers")
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    /**
     * Find customers by electrician ID
     */
    @Cacheable(key = "'electrician:' + #electricianId")
    public List<Customer> findByElectricianId(@NonNull Long electricianId) {
        return customerRepository.findByElectricianId(electricianId);
    }

    /**
     * Check if customer exists - cached
     */
    @Cacheable(key = "'exists:' + #id")
    public boolean existsById(@NonNull Long id) {
        return customerRepository.existsById(id);
    }

    // ========== WRITE OPERATIONS ==========

    /**
     * Save new customer - evicts allCustomers cache, caches the new customer
     */
    @Caching(
        put = @CachePut(key = "#result.id"),
        evict = {
            @CacheEvict(value = "allCustomers", allEntries = true),
            @CacheEvict(key = "'email:' + #customer.email", condition = "#customer.email != null")
        }
    )
    public @NonNull Customer save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }

    /**
     * Update existing customer - updates cache, evicts related caches
     */
    @Caching(
        put = @CachePut(key = "#id"),
        evict = {
            @CacheEvict(value = "allCustomers", allEntries = true),
            @CacheEvict(key = "'email:' + #customer.email", condition = "#customer.email != null"),
            @CacheEvict(key = "'electrician:' + #customer.electrician.id", condition = "#customer.electrician != null")
        }
    )
    public @NonNull Customer update(@NonNull Long id, @NonNull Customer customer) {
        customer.setId(id);
        return customerRepository.save(customer);
    }

    /**
     * Delete customer - evicts all related caches
     */
    @Caching(evict = {
        @CacheEvict(key = "#id"),
        @CacheEvict(value = "allCustomers", allEntries = true),
        @CacheEvict(key = "'email:' + #result?.email", condition = "#result != null", beforeInvocation = false),
        @CacheEvict(key = "'exists:' + #id")
    })
    public Optional<Customer> deleteById(@NonNull Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        customerRepository.deleteById(id);
        return customer;
    }

    // ========== BULK OPERATIONS ==========

    /**
     * Save multiple customers
     */
    @CacheEvict(value = "allCustomers", allEntries = true)
    public @NonNull List<Customer> saveAll(@NonNull List<Customer> customers) {
        return customerRepository.saveAll(customers);
    }

    /**
     * Clear all customer caches manually
     */
    @CacheEvict(value = {"customers", "allCustomers"}, allEntries = true)
    public void clearCache() {
        // Method to manually clear all customer caches
    }

    // ========== FALLBACK / DIRECT ACCESS ==========

    /**
     * Direct repository access - bypass cache when needed
     */
    public CustomerRepository getRepository() {
        return customerRepository;
    }
}