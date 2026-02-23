package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.PartUsed;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartUsedRepository extends JpaRepository<PartUsed, Long> {
    // Optional: Find all parts used for a specific job
    List<PartUsed> findByJobId(Long jobId);
}
