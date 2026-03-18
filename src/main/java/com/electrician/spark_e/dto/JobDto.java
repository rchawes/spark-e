package com.electrician.spark_e.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Job creation and updates
 * Provides comprehensive input validation
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class JobDto {

    @NotNull(message = "Customer ID is required")
    @Positive(message = "Customer ID must be positive")
    private Long customerId;

    @NotNull(message = "Electrician ID is required")
    @Positive(message = "Electrician ID must be positive")
    private Long electricianId;

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 100, message = "Job title must be between 3 and 100 characters")
    private String jobTitle;

    @NotBlank(message = "Job description is required")
    @Size(min = 10, max = 1000, message = "Job description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Job status is required")
    private String status;

    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @DecimalMin(value = 0.0, inclusive = true, message = "Estimated cost cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Estimated cost must have max 10 integer and 2 decimal digits")
    private java.math.BigDecimal estimatedCost;

    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    @Size(max = 100, message = "Priority must not exceed 100 characters")
    private String priority;
}
