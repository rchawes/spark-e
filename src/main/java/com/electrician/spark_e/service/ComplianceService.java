package com.electrician.spark_e.service;

import com.electrician.spark_e.model.*;
import com.electrician.spark_e.repository.ComplianceChecklistRepository;
import com.electrician.spark_e.repository.JobRepository;
import com.electrician.spark_e.repository.ChecklistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplianceService {

    @Autowired
    private ComplianceChecklistRepository checklistRepository;

    @Autowired
    private JobRepository jobRepository;          // single declaration

    @Autowired
    private ChecklistItemRepository checklistItemRepository;

    // Get the checklist for a job based on its job type
    public ComplianceChecklist getChecklistForJob(Job job) {
        if (job.getJobType() == null) {
            return null;
        }
        return job.getJobType().getComplianceChecklist();
    }

    // Mark a checklist item as completed for a job
    @Transactional
    public Job completeChecklistItem(Long jobId, Long itemId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        ChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Checklist item not found"));

        if (!job.getCompletedChecklistItems().contains(item)) {
            job.getCompletedChecklistItems().add(item);
        }
        return jobRepository.save(job);
    }

    // Generate a compliance report for a job
    public ComplianceReport generateReport(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        ComplianceReport report = new ComplianceReport();
        report.setJobId(jobId);
        report.setCustomerName(job.getCustomer().getFirstName() + " " + job.getCustomer().getLastName());
        report.setJobDate(job.getScheduledDate());
        report.setElectricianName(job.getElectrician().getName());

        ComplianceChecklist checklist = getChecklistForJob(job);
        if (checklist != null) {
            List<ChecklistItem> required = checklist.getItems();
            List<ChecklistItem> completed = job.getCompletedChecklistItems();

            report.setAllChecksCompleted(completed.containsAll(required));
            report.setMissingChecks(required.stream()
                    .filter(item -> !completed.contains(item))
                    .collect(Collectors.toList()));
        } else {
            report.setAllChecksCompleted(true); // no checklist required
            report.setMissingChecks(List.of());
        }

        return report;
    }

    // Inner class for report DTO
    public static class ComplianceReport {
        private Long jobId;
        private String customerName;
        private LocalDateTime jobDate;
        private String electricianName;
        private boolean allChecksCompleted;
        private List<ChecklistItem> missingChecks;

        // getters and setters
        public Long getJobId() { return jobId; }
        public void setJobId(Long jobId) { this.jobId = jobId; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public LocalDateTime getJobDate() { return jobDate; }
        public void setJobDate(LocalDateTime jobDate) { this.jobDate = jobDate; }

        public String getElectricianName() { return electricianName; }
        public void setElectricianName(String electricianName) { this.electricianName = electricianName; }

        public boolean isAllChecksCompleted() { return allChecksCompleted; }
        public void setAllChecksCompleted(boolean allChecksCompleted) { this.allChecksCompleted = allChecksCompleted; }

        public List<ChecklistItem> getMissingChecks() { return missingChecks; }
        public void setMissingChecks(List<ChecklistItem> missingChecks) { this.missingChecks = missingChecks; }
    }
}