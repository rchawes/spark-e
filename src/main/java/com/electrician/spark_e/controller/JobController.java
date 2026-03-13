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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/recent")
    public ResponseEntity<?> getRecentJobs() {
        try {
            // Get recent jobs (limited to 10, sorted by creation date)
            Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
            List<Job> recentJobs = jobRepository.findAll(pageable).getContent();

            return ResponseEntity.ok(recentJobs);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to load recent jobs"));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@NonNull @PathVariable Long id) {
        return jobRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createJob(@RequestBody Job job) {
        try {
            // Handle customer and electrician relationships
            if (job.getCustomer() != null && job.getCustomer().getId() != null) {
                Long customerId = job.getCustomer().getId();
                if (customerId != null) {
                    Customer customer = customerRepository.findById(customerId).orElse(null);
                    if (customer == null) {
                        return ResponseEntity.badRequest().body("Customer not found");
                    }
                    job.setCustomer(customer);
                }
            }
            
            if (job.getElectrician() != null && job.getElectrician().getId() != null) {
                Long electricianId = job.getElectrician().getId();
                if (electricianId != null) {
                    Electrician electrician = electricianRepository.findById(electricianId).orElse(null);
                    if (electrician == null) {
                        return ResponseEntity.badRequest().body("Electrician not found");
                    }
                    job.setElectrician(electrician);
                }
            }
            
            // Set default status
            if (job.getStatus() == null) {
                job.setStatus(JobStatus.SCHEDULED);
            }
            
            Job savedJob = jobRepository.save(job);
            return ResponseEntity.ok(savedJob);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating job: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJob(@NonNull @PathVariable Long id, @RequestBody Job jobDetails) {
        return jobRepository.findById(id)
                .map(job -> {
                    // Update simple fields
                    job.setDescription(jobDetails.getDescription());
                    job.setScheduledDate(jobDetails.getScheduledDate());
                    job.setCompletedDate(jobDetails.getCompletedDate());
                    job.setHoursWorked(jobDetails.getHoursWorked());
                    job.setStatus(jobDetails.getStatus());
                    
                    // Update relationships safely
                    if (jobDetails.getCustomer() != null && jobDetails.getCustomer().getId() != null) {
                        Long customerId = jobDetails.getCustomer().getId();
                        if (customerId != null) {
                            Customer customer = customerRepository.findById(customerId).orElse(null);
                            if (customer != null) job.setCustomer(customer);
                        }
                    }
                    if (jobDetails.getElectrician() != null && jobDetails.getElectrician().getId() != null) {
                        Long electricianId = jobDetails.getElectrician().getId();
                        if (electricianId != null) {
                            Electrician electrician = electricianRepository.findById(electricianId).orElse(null);
                            if (electrician != null) job.setElectrician(electrician);
                        }
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
    public ResponseEntity<Void> deleteJob(@NonNull @PathVariable Long id) {
        if (jobRepository.existsById(id)) {
            jobRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // Endpoint to manually generate invoice for a job
    @PostMapping("/{id}/generate-invoice")
    public ResponseEntity<?> generateInvoiceForJob(@NonNull @PathVariable Long id) {
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
