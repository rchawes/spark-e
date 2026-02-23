package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.ComplianceChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplianceChecklistRepository extends JpaRepository<ComplianceChecklist, Long> {
}