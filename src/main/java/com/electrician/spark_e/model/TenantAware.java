package com.electrician.spark_e.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Multi-tenant base entity with company isolation
 * All business entities should extend this for proper tenant separation
 */
@MappedSuperclass
@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class TenantAware extends Auditable {

    /**
     * Company/tenant identifier for data isolation
     * This ensures complete data separation between different companies
     */
    @Column(name = "company_id", nullable = false, updatable = false)
    private Long companyId;

    /**
     * Tenant name for logging and debugging
     */
    @Column(name = "tenant_name", length = 100)
    private String tenantName;

    /**
     * Whether this entity is shared across all tenants (admin only)
     */
    @Column(name = "is_shared", nullable = false)
    private Boolean isShared = false;

    /**
     * Region/branch identifier for multi-location companies
     */
    @Column(name = "branch_id")
    private Long branchId;

    /**
     * Department identifier for internal organization
     */
    @Column(name = "department_id")
    private Long departmentId;
}
