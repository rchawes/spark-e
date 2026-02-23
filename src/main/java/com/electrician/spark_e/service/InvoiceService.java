package com.electrician.spark_e.service;

import com.electrician.spark_e.model.*;
import com.electrician.spark_e.repository.InvoiceRepository;
import com.electrician.spark_e.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PartRepository partRepository;  // Stock reduction use

    @Transactional
    public Invoice generateInvoiceForJob(Job job) {
        // Check if invoice already exists
        if (invoiceRepository.findByJobId(job.getId()).isPresent()) {
            throw new RuntimeException("Invoice already exists for this job");
        }

        // Calculate labor cost
        double laborCost = 0.0;
        if (job.getHoursWorked() != null && job.getElectrician().getHourlyRate() != null) {
            laborCost = job.getHoursWorked() * job.getElectrician().getHourlyRate();
        }

        // Calculate materials cost from parts used
        double materialsCost = 0.0;
        for (PartUsed pu : job.getPartsUsed()) {
            materialsCost += pu.getQuantity() * pu.getUnitPriceAtTime();
        }

        // Calculate tax (e.g., 20% VAT)
        double subtotal = laborCost + materialsCost;
        double tax = subtotal * 0.20;  // Change config later

        // Total
        double total = subtotal + tax;

        // Generate invoice number (simple format: INV-YYYY-####)
        long count = invoiceRepository.count() + 1;
        String year = String.valueOf(LocalDate.now().getYear());
        String invoiceNumber = String.format("INV-%s-%04d", year, count);

        // Create and populate invoice
        Invoice invoice = new Invoice();
        invoice.setJob(job);
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setIssueDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30)); // net 30 days
        invoice.setLaborCost(laborCost);
        invoice.setMaterialsCost(materialsCost);
        invoice.setTax(tax);
        invoice.setTotalAmount(total);
        invoice.setPaid(false);
        invoice.setPaidDate(null);

        // Optional: Reduce stock for parts used
        reduceStockForJob(job);

        return invoiceRepository.save(invoice);
    }

    private void reduceStockForJob(Job job) {
        for (PartUsed pu : job.getPartsUsed()) {
            Part part = pu.getPart();
            part.setStockQuantity(part.getStockQuantity() - pu.getQuantity());
            partRepository.save(part);
        }
    }

    // Add other methods like markAsPaid() later
}
