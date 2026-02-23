package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class ChecklistItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;      // e.g., "Verify main bonding is in place"
    private Integer displayOrder;    // for ordering

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private ComplianceChecklist checklist;

    public ChecklistItem() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public ComplianceChecklist getChecklist() { return checklist; }
    public void setChecklist(ComplianceChecklist checklist) { this.checklist = checklist; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChecklistItem that = (ChecklistItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ChecklistItem{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}