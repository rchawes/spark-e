package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class ComplianceChecklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;                // e.g., "Consumer Unit Safety Checks"
    private String regulationReference;  // e.g., "BS 7671 Section 421"

    @ManyToOne
    @JoinColumn(name = "job_type_id")
    private JobType jobType;             // which job type this checklist applies to

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> items = new ArrayList<>();

    public ComplianceChecklist() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRegulationReference() { return regulationReference; }
    public void setRegulationReference(String regulationReference) { this.regulationReference = regulationReference; }

    public JobType getJobType() { return jobType; }
    public void setJobType(JobType jobType) { this.jobType = jobType; }

    public List<ChecklistItem> getItems() { return items; }
    public void setItems(List<ChecklistItem> items) { this.items = items; }

    // Helper to add item
    public void addItem(ChecklistItem item) {
        items.add(item);
        item.setChecklist(this);
    }

    // Helper to remove item
    public void removeItem(ChecklistItem item) {
        items.remove(item);
        item.setChecklist(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceChecklist that = (ComplianceChecklist) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ComplianceChecklist{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}