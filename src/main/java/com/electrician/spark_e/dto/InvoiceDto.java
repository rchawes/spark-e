package com.electrician.spark_e.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Invoice creation and updates
 * Provides comprehensive input validation
 */
@Data
@NoArgsConstructor
@SuperBuilder
public class InvoiceDto {

    @NotNull(message = "Job ID is required")
    @Positive(message = "Job ID must be positive")
    private Long jobId;

    @NotBlank(message = "Invoice number is required")
    @Size(min = 3, max = 50, message = "Invoice number must be between 3 and 50 characters")
    private String invoiceNumber;

    @NotNull(message = "Issue date is required")
    @PastOrPresent(message = "Issue date cannot be in the future")
    private LocalDate issueDate;

    @NotNull(message = "Due date is required")
    private LocalDate dueDate;

    private LocalDate paidDate;

    @NotNull(message = "Labor cost is required")
    @DecimalMin(value = 0.0, inclusive = false, message = "Labor cost must be positive")
    @Digits(integer = 10, fraction = 2, message = "Labor cost must have max 10 integer and 2 decimal digits")
    private BigDecimal laborCost;

    @NotNull(message = "Materials cost is required")
    @DecimalMin(value = 0.0, inclusive = false, message = "Materials cost must be positive")
    @Digits(integer = 10, fraction = 2, message = "Materials cost must have max 10 integer and 2 decimal digits")
    private BigDecimal materialsCost;

    @NotNull(message = "Tax is required")
    @DecimalMin(value = 0.0, inclusive = true, message = "Tax cannot be negative")
    @Digits(integer = 10, fraction = 2, message = "Tax must have max 10 integer and 2 decimal digits")
    private BigDecimal tax;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = 0.0, inclusive = false, message = "Total amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Total amount must have max 10 integer and 2 decimal digits")
    private BigDecimal totalAmount;

    @NotNull(message = "Paid status is required")
    private Boolean paid;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
