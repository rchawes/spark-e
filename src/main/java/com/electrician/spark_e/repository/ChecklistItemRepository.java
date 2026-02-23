package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {

}