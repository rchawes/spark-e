package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRepository extends JpaRepository<Part, Long> {
    List<Part> findByStockQuantityLessThanEqual(Integer threshold);
}