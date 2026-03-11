package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
//  Basic CRUD (Create, Read, Update, Delete)
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByElectricianId(Long electricianId);
}
