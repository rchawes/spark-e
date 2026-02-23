package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class PartUsed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private Part part;

    private Integer quantity;
    private Double unitPriceAtTime;

    public PartUsed() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public Part getPart() { return part; }
    public void setPart(Part part) { this.part = part; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Double getUnitPriceAtTime() { return unitPriceAtTime; }
    public void setUnitPriceAtTime(Double unitPriceAtTime) { this.unitPriceAtTime = unitPriceAtTime; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartUsed partUsed = (PartUsed) o;
        return Objects.equals(id, partUsed.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PartUsed{" +
                "id=" + id +
                ", part=" + (part != null ? part.getName() : "null") +
                ", quantity=" + quantity +
                ", unitPriceAtTime=" + unitPriceAtTime +
                '}';
    }
}