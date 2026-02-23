package com.electrician.spark_e.controller;

import com.electrician.spark_e.model.Customer;
import com.electrician.spark_e.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public List<Customer> GetAllCustomers() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> GetCustomerById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Customer CreateCustomer(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> UpdateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        return customerRepository.findById(id)
                .map(customer -> {
                    customer.setFirstName(customerDetails.getFirstName());
                    customer.setLastName(customerDetails.getLastName());
                    customer.setEmail(customerDetails.getEmail());
                    customer.setPhone(customerDetails.getPhone());
                    customer.setAddress(customerDetails.getAddress());
                    return ResponseEntity.ok(customerRepository.save(customer));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> DeleteCustomer(@PathVariable Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}
