package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.ComplianceChecklist;
import com.electrician.spark_e.model.Job;
import com.electrician.spark_e.repository.JobRepository;
import com.electrician.spark_e.service.ComplianceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobs/{jobId}/compliance")
public class JobComplianceController {

    @Autowired
    private ComplianceService complianceService;

    @Autowired
    private JobRepository jobRepository;

    @GetMapping("/checklist")
    public ResponseEntity<?> getChecklistForJob(@NonNull @PathVariable Long jobId) {
        return jobRepository.findById(jobId)
                .map(job -> {
                    ComplianceChecklist checklist = complianceService.getChecklistForJob(job);
                    if (checklist == null) {
                        return ResponseEntity.ok("No checklist required for this job type.");
                    }
                    return ResponseEntity.ok(checklist);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/items/{itemId}/complete")
    public ResponseEntity<?> completeChecklistItem(@NonNull @PathVariable Long jobId, @NonNull @PathVariable Long itemId) {
        try {
            Job updatedJob = complianceService.completeChecklistItem(jobId, itemId);
            return ResponseEntity.ok(updatedJob);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/report")
    public ResponseEntity<?> getComplianceReport(@NonNull @PathVariable Long jobId) {
        try {
            ComplianceService.ComplianceReport report = complianceService.generateReport(jobId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}