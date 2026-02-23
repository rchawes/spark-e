package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.Electrician;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElectricianRepository extends JpaRepository<Electrician,Long> {
}
