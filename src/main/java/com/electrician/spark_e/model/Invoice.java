package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "job_id", unique = true)
    private Job job;

    private String invoiceNumber;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private Double laborCost;
    private Double materialsCost;
    private Double tax;
    private Double totalAmount;
    private Boolean paid;
    private LocalDate paidDate;

    public Invoice() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Double getLaborCost() { return laborCost; }
    public void setLaborCost(Double laborCost) { this.laborCost = laborCost; }

    public Double getMaterialsCost() { return materialsCost; }
    public void setMaterialsCost(Double materialsCost) { this.materialsCost = materialsCost; }

    public Double getTax() { return tax; }
    public void setTax(Double tax) { this.tax = tax; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Boolean getPaid() { return paid; }
    public void setPaid(Boolean paid) { this.paid = paid; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", invoiceNumber='" + invoiceNumber + '\'' +
                ", issueDate=" + issueDate +
                ", dueDate=" + dueDate +
                ", totalAmount=" + totalAmount +
                ", paid=" + paid +
                '}';
    }
}