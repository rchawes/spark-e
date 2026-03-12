package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Invoice;
import com.electrician.spark_e.model.Job;
import com.electrician.spark_e.repository.InvoiceRepository;
import com.electrician.spark_e.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private JobRepository jobRepository;

    // GET all invoices
    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // GET invoice by ID
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@NonNull @PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET invoice by Job ID
    @GetMapping("/job/{jobId}")
    public ResponseEntity<Invoice> getInvoiceByJobId(@NonNull @PathVariable Long jobId) {
        return invoiceRepository.findByJobId(jobId)   // uses the @Query method we added
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT to mark invoice as paid
    @PutMapping("/{id}/pay")
    public ResponseEntity<Invoice> markInvoiceAsPaid(@NonNull @PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    invoice.setPaid(true);
                    invoice.setPaidDate(java.time.LocalDate.now());
                    return invoiceRepository.save(invoice);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT to update invoice
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInvoice(@NonNull @PathVariable Long id, @RequestBody Invoice invoiceDetails) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    // Update invoice fields
                    invoice.setJob(invoiceDetails.getJob());
                    invoice.setInvoiceNumber(invoiceDetails.getInvoiceNumber());
                    invoice.setIssueDate(invoiceDetails.getIssueDate());
                    invoice.setDueDate(invoiceDetails.getDueDate());
                    invoice.setLaborCost(invoiceDetails.getLaborCost());
                    invoice.setMaterialsCost(invoiceDetails.getMaterialsCost());
                    invoice.setTax(invoiceDetails.getTax());
                    invoice.setTotalAmount(invoiceDetails.getTotalAmount());
                    invoice.setPaid(invoiceDetails.getPaid());
                    // Only update paid date if status changed to paid
                    if (invoiceDetails.getPaid() && (invoice.getPaidDate() == null)) {
                        invoice.setPaidDate(java.time.LocalDate.now());
                    }
                    return ResponseEntity.ok(invoiceRepository.save(invoice));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // POST to create new invoice
    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody Invoice invoice) {
        try {
            // If invoice has jobId but no job object, fetch the job
            if (invoice.getJob() == null && invoice.getJobId() != null) {
                Job job = jobRepository.findById(invoice.getJobId())
                        .orElseThrow(() -> new RuntimeException("Job not found with ID: " + invoice.getJobId()));
                invoice.setJob(job);
            }
            
            Invoice savedInvoice = invoiceRepository.save(invoice);
            return ResponseEntity.ok(savedInvoice);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to create invoice: " + e.getMessage()));
        }
    }

    // DELETE to remove invoice
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvoice(@NonNull @PathVariable Long id) {
        if (invoiceRepository.existsById(id)) {
            invoiceRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}