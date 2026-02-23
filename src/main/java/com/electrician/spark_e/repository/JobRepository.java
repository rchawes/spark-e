package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Long> {
    // Add custom query methods later
    List<Job> findByElectricianIdAndScheduledDateBetween(
            Long electricianId, LocalDateTime start, LocalDateTime end);
}
