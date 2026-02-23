package com.electrician.spark_e.repository;

import com.electrician.spark_e.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
// This is our basic CRUD (Create, Read, Update, Delete)
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
