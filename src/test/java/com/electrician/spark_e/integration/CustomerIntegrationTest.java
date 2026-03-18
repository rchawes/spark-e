package com.electrician.spark_e.integration;

import com.electrician.spark_e.model.Customer;
import com.electrician.spark_e.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Customer entity using TestContainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("Customer Integration Tests")
public class CustomerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass")
            .withReuse(true);

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        // Configure test database
        DynamicPropertyRegistry.setProperty("spring.datasource.url", postgres.getJdbcUrl());
        DynamicPropertyRegistry.setProperty("spring.datasource.username", postgres.getUsername());
        DynamicPropertyRegistry.setProperty("spring.datasource.password", postgres.getPassword());
        DynamicPropertyRegistry.setProperty("spring.datasource.driver-class-name", "org.postgresql.Driver");
        
        // Clean up before each test
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create customer with valid data")
    void shouldCreateCustomerWithValidData() {
        // Given
        Customer customer = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("123-456-7890")
                .address("123 Main St")
                .companyName("Acme Corp")
                .notes("Test customer")
                .isActive(true)
                .build();

        // When
        Customer savedCustomer = customerRepository.save(customer);

        // Then
        assertNotNull(savedCustomer.getId());
        assertEquals("John", savedCustomer.getFirstName());
        assertEquals("Doe", savedCustomer.getLastName());
        assertEquals("john.doe@example.com", savedCustomer.getEmail());
        assertNotNull(savedCustomer.getCreatedAt());
        assertNotNull(savedCustomer.getUpdatedAt());
        assertNotNull(savedCustomer.getCreatedBy());
    }

    @Test
    @DisplayName("Should find customers with pagination")
    void shouldFindCustomersWithPagination() {
        // Given
        for (int i = 0; i < 25; i++) {
            Customer customer = Customer.builder()
                    .firstName("Test" + i)
                    .lastName("User" + i)
                    .email("test" + i + "@example.com")
                    .isActive(true)
                    .build();
            customerRepository.save(customer);
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        // When
        Page<Customer> result = customerRepository.findAll(pageable);

        // Then
        assertEquals(25, result.getTotalElements());
        assertEquals(10, result.getContent().size());
        assertEquals(3, result.getTotalPages());
    }

    @Test
    @DisplayName("Should search customers by name")
    void shouldSearchCustomersByName() {
        // Given
        Customer customer1 = Customer.builder()
                .firstName("Alice")
                .lastName("Smith")
                .email("alice@example.com")
                .isActive(true)
                .build();
        Customer customer2 = Customer.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .email("bob@example.com")
                .isActive(true)
                .build();
        
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Customer> result = customerRepository.searchCustomers("Alice", pageable);

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Alice", result.getContent().get(0).getFirstName());
    }

    @Test
    @DisplayName("Should update customer successfully")
    void shouldUpdateCustomerSuccessfully() {
        // Given
        Customer customer = Customer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("jane@example.com")
                .isActive(true)
                .build();
        Customer savedCustomer = customerRepository.save(customer);

        // When
        savedCustomer.setFirstName("Jane Updated");
        savedCustomer.setNotes("Updated notes");
        Customer updatedCustomer = customerRepository.save(savedCustomer);

        // Then
        assertEquals("Jane Updated", updatedCustomer.getFirstName());
        assertEquals("Updated notes", updatedCustomer.getNotes());
        assertTrue(updatedCustomer.getUpdatedAt().isAfter(updatedCustomer.getCreatedAt()));
    }

    @Test
    @DisplayName("Should validate email uniqueness")
    void shouldValidateEmailUniqueness() {
        // Given
        Customer customer1 = Customer.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .isActive(true)
                .build();
        customerRepository.save(customer1);

        Customer customer2 = Customer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email("john@example.com") // Same email
                .isActive(true)
                .build();

        // When & Then
        assertThrows(Exception.class, () -> customerRepository.save(customer2));
    }

    @Test
    @DisplayName("Should get customer statistics")
    void shouldGetCustomerStatistics() {
        // Given
        Customer activeCustomer = Customer.builder()
                .firstName("Active")
                .lastName("User")
                .email("active@example.com")
                .isActive(true)
                .build();
        Customer inactiveCustomer = Customer.builder()
                .firstName("Inactive")
                .lastName("User")
                .email("inactive@example.com")
                .isActive(false)
                .build();
        
        customerRepository.save(activeCustomer);
        customerRepository.save(inactiveCustomer);

        // When
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countByIsActiveTrue();

        // Then
        assertEquals(2, totalCustomers);
        assertEquals(1, activeCustomers);
    }
}
