package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class JobType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // e.g., "Consumer Unit Replacement"
    private String description; // optional

    @OneToOne(mappedBy = "jobType")
    private ComplianceChecklist complianceChecklist;

    // Getter and setter
    public ComplianceChecklist getComplianceChecklist() { return complianceChecklist; }
    public void setComplianceChecklist(ComplianceChecklist complianceChecklist) { this.complianceChecklist = complianceChecklist; }

    @OneToMany(mappedBy = "jobType")
    private List<Job> jobs;

    // No-args constructor
    public JobType() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Job> getJobs() { return jobs; }
    public void setJobs(List<Job> jobs) { this.jobs = jobs; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobType jobType = (JobType) o;
        return Objects.equals(id, jobType.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "JobType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}