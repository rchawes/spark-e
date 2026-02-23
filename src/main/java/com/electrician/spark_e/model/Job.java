package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private LocalDateTime scheduledDate;
    private LocalDateTime completedDate;
    private Double hoursWorked;



    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @ManyToOne
    @JoinColumn(name = "job_type_id")
    private JobType jobType;

    // Add getter and setter
    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    @ManyToMany
    @JoinTable(
            name = "job_completed_checklist_items",
            joinColumns = @JoinColumn(name = "job_id"),
            inverseJoinColumns = @JoinColumn(name = "checklist_item_id")
    )
    private List<ChecklistItem> completedChecklistItems = new ArrayList<>();

    // Getter and setter
    public List<ChecklistItem> getCompletedChecklistItems() { return completedChecklistItems; }
    public void setCompletedChecklistItems(List<ChecklistItem> completedChecklistItems) { this.completedChecklistItems = completedChecklistItems; }

    // Helper method to check if all required items are completed
    public boolean isChecklistComplete() {
        if (jobType == null || jobType.getComplianceChecklist() == null) {
            return true; // no checklist required
        }
        ComplianceChecklist required = jobType.getComplianceChecklist();
        List<ChecklistItem> requiredItems = required.getItems();
        return completedChecklistItems.containsAll(requiredItems);
    }

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "electrician_id")
    private Electrician electrician;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<PartUsed> partsUsed = new ArrayList<>();

    public Job() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDateTime getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDateTime completedDate) { this.completedDate = completedDate; }

    public Double getHoursWorked() { return hoursWorked; }
    public void setHoursWorked(Double hoursWorked) { this.hoursWorked = hoursWorked; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Electrician getElectrician() { return electrician; }
    public void setElectrician(Electrician electrician) { this.electrician = electrician; }

    public List<PartUsed> getPartsUsed() { return partsUsed; }
    public void setPartsUsed(List<PartUsed> partsUsed) { this.partsUsed = partsUsed; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", scheduledDate=" + scheduledDate +
                ", completedDate=" + completedDate +
                ", hoursWorked=" + hoursWorked +
                ", status=" + status +
                '}';
    }
}