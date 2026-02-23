package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("SELECT i FROM Invoice i WHERE i.job.id = :jobId")
    Optional<Invoice> findByJobId(@Param("jobId") Long jobId);

    List<Invoice> findByJobElectricianIdAndIssueDateBetween(Long electricianId, LocalDate start, LocalDate end);

    List<Invoice> findByJobCustomerId(Long customerId);
}
