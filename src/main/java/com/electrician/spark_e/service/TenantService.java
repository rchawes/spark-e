package com.electrician.spark_e.service;

import com.electrician.spark_e.model.Company;
import com.electrician.spark_e.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Multi-tenant management service
 * Handles tenant isolation and data access control
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final CompanyRepository companyRepository;
    private final HttpServletRequest request;

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String SUBDOMAIN_HEADER = "X-Subdomain";
    private static final String DEFAULT_TENANT = "default";

    /**
     * Get current tenant ID from request context
     */
    public Long getCurrentTenantId() {
        // Try multiple methods to determine tenant
        return getTenantFromHeader()
                .or(this::getTenantFromSubdomain)
                .or(this::getTenantFromUserContext)
                .orElseThrow(() -> new TenantException("Unable to determine tenant context"));
    }

    /**
     * Get current tenant entity
     */
    @Transactional(readOnly = true)
    public Company getCurrentTenant() {
        Long tenantId = getCurrentTenantId();
        return companyRepository.findById(tenantId)
                .orElseThrow(() -> new TenantException("Tenant not found: " + tenantId));
    }

    /**
     * Check if user has access to current tenant
     */
    public boolean hasTenantAccess(Long tenantId) {
        try {
            Long currentTenantId = getCurrentTenantId();
            return currentTenantId.equals(tenantId);
        } catch (TenantException e) {
            return false;
        }
    }

    /**
     * Validate tenant access for data operations
     */
    public void validateTenantAccess(Long tenantId) {
        if (!hasTenantAccess(tenantId)) {
            throw new TenantException("Access denied: Invalid tenant context");
        }
    }

    /**
     * Create new tenant (company)
     */
    @Transactional
    public Company createTenant(Company company) {
        log.info("Creating new tenant: {}", company.getName());
        
        // Validate tenant uniqueness
        if (companyRepository.existsBySubdomain(company.getSubdomain())) {
            throw new TenantException("Subdomain already exists: " + company.getSubdomain());
        }

        // Set default values
        company.setActive(true);
        company.setPlanType("STARTER"); // Default plan
        
        Company savedTenant = companyRepository.save(company);
        log.info("Tenant created successfully: {} (ID: {})", savedTenant.getName(), savedTenant.getId());
        
        return savedTenant;
    }

    /**
     * Get tenant from HTTP header
     */
    private Optional<Long> getTenantFromHeader() {
        String tenantHeader = request.getHeader(TENANT_HEADER);
        if (tenantHeader != null && !tenantHeader.isEmpty()) {
            try {
                return Optional.of(Long.parseLong(tenantHeader));
            } catch (NumberFormatException e) {
                log.warn("Invalid tenant ID in header: {}", tenantHeader);
            }
        }
        return Optional.empty();
    }

    /**
     * Get tenant from subdomain
     */
    private Optional<Long> getTenantFromSubdomain() {
        String subdomain = request.getHeader(SUBDOMAIN_HEADER);
        if (subdomain == null) {
            // Extract from hostname
            String host = request.getServerName();
            if (host != null && host.contains(".")) {
                subdomain = host.split("\\.")[0];
            }
        }

        if (subdomain != null && !subdomain.isEmpty() && !subdomain.equals("www")) {
            return companyRepository.findBySubdomain(subdomain)
                    .map(Company::getId);
        }

        return Optional.empty();
    }

    /**
     * Get tenant from authenticated user context
     */
    private Optional<Long> getTenantFromUserContext() {
        // This would integrate with Spring Security context
        // For now, return default tenant
        return Optional.of(1L); // Default tenant for demo
    }

    /**
     * Check if tenant exists and is active
     */
    @Transactional(readOnly = true)
    public boolean isValidTenant(Long tenantId) {
        return companyRepository.existsByIdAndActiveTrue(tenantId);
    }

    /**
     * Get tenant statistics
     */
    @Transactional(readOnly = true)
    public TenantStats getTenantStats(Long tenantId) {
        validateTenantAccess(tenantId);
        
        Company tenant = getCurrentTenant();
        return TenantStats.builder()
                .tenantId(tenantId)
                .tenantName(tenant.getName())
                .subdomain(tenant.getSubdomain())
                .planType(tenant.getPlanType())
                .active(tenant.getActive())
                .maxUsers(tenant.getMaxUsers())
                .maxCustomers(tenant.getMaxCustomers())
                .maxJobs(tenant.getMaxJobs())
                .createdAt(tenant.getCreatedAt())
                .build();
    }

    /**
     * Tenant statistics data class
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TenantStats {
        private Long tenantId;
        private String tenantName;
        private String subdomain;
        private String planType;
        private Boolean active;
        private Integer maxUsers;
        private Integer maxCustomers;
        private Integer maxJobs;
        private java.time.LocalDateTime createdAt;
    }

    /**
     * Custom exception for tenant-related errors
     */
    public static class TenantException extends RuntimeException {
        public TenantException(String message) {
            super(message);
        }

        public TenantException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
