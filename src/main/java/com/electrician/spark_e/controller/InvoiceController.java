package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Invoice;
import com.electrician.spark_e.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    // GET all invoices
    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // GET invoice by ID
    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET invoice by Job ID
    @GetMapping("/job/{jobId}")
    public ResponseEntity<Invoice> getInvoiceByJobId(@PathVariable Long jobId) {
        return invoiceRepository.findByJobId(jobId)   // uses the @Query method we added
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT to mark invoice as paid
    @PutMapping("/{id}/pay")
    public ResponseEntity<Invoice> markInvoiceAsPaid(@PathVariable Long id) {
        return invoiceRepository.findById(id)
                .map(invoice -> {
                    invoice.setPaid(true);
                    invoice.setPaidDate(java.time.LocalDate.now());
                    return invoiceRepository.save(invoice);
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}