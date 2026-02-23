package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Job;
import com.electrician.spark_e.model.JobStatus;
import com.electrician.spark_e.model.Customer;
import com.electrician.spark_e.model.Electrician;
import com.electrician.spark_e.model.Invoice;
import com.electrician.spark_e.repository.JobRepository;
import com.electrician.spark_e.repository.CustomerRepository;
import com.electrician.spark_e.repository.ElectricianRepository;
import com.electrician.spark_e.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ElectricianRepository electricianRepository;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        // Validate that customer and electrician exist
        if (job.getCustomer() == null || job.getCustomer().getId() == null) {
            return ResponseEntity.badRequest().body("Customer ID is required");
        }
        if (job.getElectrician() == null || job.getElectrician().getId() == null) {
            return ResponseEntity.badRequest().body("Electrician ID is required");
        }

        // Fetch full entities from database
        Customer customer = customerRepository.findById(job.getCustomer().getId())
                .orElse(null);
        Electrician electrician = electricianRepository.findById(job.getElectrician().getId())
                .orElse(null);

        if (customer == null) {
            return ResponseEntity.badRequest().body("Customer not found");
        }
        if (electrician == null) {
            return ResponseEntity.badRequest().body("Electrician not found");
        }

        job.setCustomer(customer);
        job.setElectrician(electrician);

        // Set default status
        if (job.getStatus() == null) {
            job.setStatus(JobStatus.SCHEDULED);
        }

        Job savedJob = jobRepository.save(job);
        return ResponseEntity.ok(savedJob);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        return jobRepository.findById(id)
                .map(job -> {
                    // Update simple fields
                    job.setDescription(jobDetails.getDescription());
                    job.setScheduledDate(jobDetails.getScheduledDate());
                    job.setCompletedDate(jobDetails.getCompletedDate());
                    job.setHoursWorked(jobDetails.getHoursWorked());
                    job.setStatus(jobDetails.getStatus());

                    // Update relationships
                    if (jobDetails.getCustomer() != null && jobDetails.getCustomer().getId() != null) {
                        Customer customer = customerRepository.findById(jobDetails.getCustomer().getId()).orElse(null);
                        if (customer != null) job.setCustomer(customer);
                    }
                    if (jobDetails.getElectrician() != null && jobDetails.getElectrician().getId() != null) {
                        Electrician electrician = electricianRepository.findById(jobDetails.getElectrician().getId()).orElse(null);
                        if (electrician != null) job.setElectrician(electrician);
                    }

                    Job updatedJob = jobRepository.save(job);

                    // If status changed to COMPLETED, automatically generate invoice
                    if (jobDetails.getStatus() == JobStatus.COMPLETED && job.getStatus() != JobStatus.COMPLETED) {
                        try {
                            invoiceService.generateInvoiceForJob(updatedJob);
                        } catch (RuntimeException e) {
                            // Log error but don't fail the update
                            System.err.println("Invoice generation failed: " + e.getMessage());
                        }
                    }

                    return updatedJob;
                })
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint to manually generate invoice for a job
    @PostMapping("/{id}/generate-invoice")
    public ResponseEntity<?> generateInvoiceForJob(@PathVariable Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    try {
                        Invoice invoice = invoiceService.generateInvoiceForJob(job);
                        return ResponseEntity.ok(invoice);
                    } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
