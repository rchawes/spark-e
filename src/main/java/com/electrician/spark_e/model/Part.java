package com.electrician.spark_e.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer stockQuantity;
    private Integer reorderThreshold;
    private Double unitPrice;

    public Part() {}

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getReorderThreshold() { return reorderThreshold; }
    public void setReorderThreshold(Integer reorderThreshold) { this.reorderThreshold = reorderThreshold; }

    public Double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Part part = (Part) o;
        return Objects.equals(id, part.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Part{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stockQuantity=" + stockQuantity +
                ", reorderThreshold=" + reorderThreshold +
                ", unitPrice=" + unitPrice +
                '}';
    }
}