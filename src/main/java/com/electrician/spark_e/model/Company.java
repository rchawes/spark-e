package com.electrician.spark_e.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Company/tenant entity for multi-tenant architecture
 * Represents individual businesses using the Spark-E platform
 */
@Entity
@Table(name = "companies", indexes = {
    @Index(name = "idx_company_subdomain", columnList = "subdomain"),
    @Index(name = "idx_company_active", columnList = "active"),
    @Index(name = "idx_company_plan", columnList = "plan_type")
})
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class Company extends Auditable {

    /**
     * Company name
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Unique subdomain for multi-tenant access
     */
    @Column(name = "subdomain", nullable = false, length = 50, unique = true)
    private String subdomain;

    /**
     * Company website
     */
    @Column(name = "website", length = 255)
    private String website;

    /**
     * Company logo URL
     */
    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    /**
     * Company contact email
     */
    @Column(name = "contact_email", nullable = false, length = 100)
    private String contactEmail;

    /**
     * Company phone number
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * Company address
     */
    @Column(name = "address", length = 500)
    private String address;

    /**
     * Company description
     */
    @Column(name = "description", length = 1000)
    private String description;

    /**
     * Industry type
     */
    @Column(name = "industry", length = 50)
    private String industry;

    /**
     * Company size (SMALL, MEDIUM, LARGE, ENTERPRISE)
     */
    @Column(name = "company_size", length = 20)
    private String companySize;

    /**
     * Subscription plan type
     */
    @Column(name = "plan_type", nullable = false, length = 20)
    private String planType;

    /**
     * Whether the company is active
     */
    @Column(name = "active", nullable = false)
    private Boolean active = true;

    /**
     * Maximum number of users allowed
     */
    @Column(name = "max_users")
    private Integer maxUsers;

    /**
     * Maximum number of customers allowed
     */
    @Column(name = "max_customers")
    private Integer maxCustomers;

    /**
     * Maximum number of jobs allowed
     */
    @Column(name = "max_jobs")
    private Integer maxJobs;

    /**
     * Maximum storage allowed (in MB)
     */
    @Column(name = "max_storage_mb")
    private Integer maxStorageMb;

    /**
     * Subscription start date
     */
    @Column(name = "subscription_start")
    private LocalDateTime subscriptionStart;

    /**
     * Subscription end date
     */
    @Column(name = "subscription_end")
    private LocalDateTime subscriptionEnd;

    /**
     * Billing cycle (MONTHLY, YEARLY)
     */
    @Column(name = "billing_cycle", length = 10)
    private String billingCycle;

    /**
     * Company settings (JSON)
     */
    @Column(name = "settings", columnDefinition = "TEXT")
    private String settings;

    /**
     * Timezone for the company
     */
    @Column(name = "timezone", length = 50)
    private String timezone;

    /**
     * Currency for the company
     */
    @Column(name = "currency", length = 3)
    private String currency;

    /**
     * Tax rate for the company
     */
    @Column(name = "tax_rate")
    private Double taxRate;

    /**
     * Whether the company has been verified
     */
    @Column(name = "verified", nullable = false)
    private Boolean verified = false;

    /**
     * Verification date
     */
    @Column(name = "verified_date")
    private LocalDateTime verifiedDate;

    /**
     * One-to-many relationship with users
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    /**
     * One-to-many relationship with customers
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Customer> customers;

    /**
     * One-to-many relationship with jobs
     */
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Job> jobs;

    /**
     * Check if company is within subscription limits
     */
    public boolean isWithinLimits(String resourceType, int currentCount) {
        if (!active || subscriptionEnd != null && subscriptionEnd.isBefore(LocalDateTime.now())) {
            return false;
        }

        return switch (resourceType.toUpperCase()) {
            case "USERS" -> maxUsers == null || currentCount < maxUsers;
            case "CUSTOMERS" -> maxCustomers == null || currentCount < maxCustomers;
            case "JOBS" -> maxJobs == null || currentCount < maxJobs;
            default -> true;
        };
    }

    /**
     * Check if subscription is active
     */
    public boolean isSubscriptionActive() {
        return active && (subscriptionEnd == null || subscriptionEnd.isAfter(LocalDateTime.now()));
    }

    /**
     * Get days until subscription expires
     */
    public long getDaysUntilExpiration() {
        if (subscriptionEnd == null) return Long.MAX_VALUE;
        return java.time.Duration.between(LocalDateTime.now(), subscriptionEnd).toDays();
    }

    /**
     * Check if company needs subscription renewal
     */
    public boolean needsRenewal() {
        return subscriptionEnd != null && getDaysUntilExpiration() <= 30;
    }
}
