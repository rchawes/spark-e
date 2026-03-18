package com.electrician.spark_e.frontend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Frontend E2E tests for critical user workflows
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Frontend E2E Tests")
@Nested
public class FrontendE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        // Configure Chrome for headless testing
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @DisplayName("Should login successfully and navigate to dashboard")
    void shouldLoginSuccessfullyAndNavigateToDashboard() {
        // Given
        String baseUrl = "http://localhost:" + port + "/spark-e.html";

        // When
        driver.get(baseUrl);

        // Then
        // Wait for page to load
        WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));
        assertNotNull(loginForm, "Login form should be present");

        // Fill in credentials
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.id("password")).sendKeys("password123");

        // Submit login
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Verify dashboard is loaded
        WebElement dashboard = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("dashboard")));
        assertNotNull(dashboard, "Dashboard should be loaded after login");
        
        // Verify user is logged in
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains("dashboard") || currentUrl.contains("spark-e.html"), 
                  "Should be on dashboard or main page after login");
    }

    @Test
    @DisplayName("Should create new customer through UI")
    void shouldCreateNewCustomerThroughUI() {
        // Given - User is logged in
        loginAndNavigateToDashboard();

        // When - Navigate to customers and create new customer
        driver.findElement(By.id("customersTab")).click();
        
        WebElement addCustomerBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[onclick*='addCustomer']")));
        addCustomerBtn.click();

        // Fill customer form
        driver.findElement(By.id("customerFirstName")).sendKeys("Test");
        driver.findElement(By.id("customerLastName")).sendKeys("User");
        driver.findElement(By.id("customerEmail")).sendKeys("test.user@example.com");
        driver.findElement(By.id("customerPhone")).sendKeys("123-456-7890");

        // Submit form
        driver.findElement(By.cssSelector("button[onclick*='saveCustomer']")).click();

        // Then - Verify customer was created
        WebElement successMessage = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("success-message")));
        assertNotNull(successMessage, "Success message should appear");
        assertTrue(successMessage.getText().contains("Customer created successfully"));
    }

    @Test
    @DisplayName("Should create job and then invoice")
    void shouldCreateJobAndThenInvoice() {
        // Given - User is logged in and customer exists
        loginAndNavigateToDashboard();
        createTestCustomer();

        // When - Create job
        driver.findElement(By.id("jobsTab")).click();
        
        WebElement addJobBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[onclick*='addJob']")));
        addJobBtn.click();

        // Fill job form
        driver.findElement(By.id("jobTitle")).sendKeys("Electrical Panel Installation");
        driver.findElement(By.id("jobDescription")).sendKeys("Install new electrical panel in basement");
        driver.findElement(By.id("jobLocation")).sendKeys("123 Main St");

        // Submit job
        driver.findElement(By.cssSelector("button[onclick*='saveJob']")).click();

        // Wait for job to be created and navigate to invoice
        WebElement jobSuccess = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("success-message")));
        assertNotNull(jobSuccess, "Job success message should appear");

        // Create invoice for the job
        WebElement createInvoiceBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button[onclick*='generateInvoice']")));
        createInvoiceBtn.click();

        // Then - Verify invoice creation modal appears
        WebElement invoiceModal = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.id("invoiceModal")));
        assertNotNull(invoiceModal, "Invoice modal should appear");

        // Fill invoice details
        driver.findElement(By.id("invoiceAmount")).sendKeys("1500.00");
        driver.findElement(By.id("invoiceNotes")).sendKeys("Electrical panel installation invoice");

        // Submit invoice
        driver.findElement(By.cssSelector("button[onclick*='saveInvoice']")).click();

        // Verify invoice was created
        WebElement invoiceSuccess = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("success-message")));
        assertNotNull(invoiceSuccess, "Invoice success message should appear");
    }

    @Test
    @DisplayName("Should handle form validation errors")
    void shouldHandleFormValidationErrors() {
        // Given - User is logged in
        loginAndNavigateToDashboard();

        // When - Try to create customer with invalid data
        driver.findElement(By.id("customersTab")).click();
        driver.findElement(By.cssSelector("button[onclick*='addCustomer']")).click();

        // Submit empty form
        driver.findElement(By.cssSelector("button[onclick*='saveCustomer']")).click();

        // Then - Validation errors should appear
        WebElement errorMessages = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("error-message")));
        assertNotNull(errorMessages, "Error messages should appear");
        assertTrue(errorMessages.getText().contains("First name is required"));
        assertTrue(errorMessages.getText().contains("Email is required"));
    }

    @Test
    @DisplayName("Should be mobile responsive")
    void shouldBeMobileResponsive() {
        // Given
        String baseUrl = "http://localhost:" + port + "/spark-e.html";

        // When - Set mobile viewport
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667)); // iPhone size

        driver.get(baseUrl);

        // Then - Mobile layout should be active
        WebElement mobileMenu = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("mobile-menu")));
        assertNotNull(mobileMenu, "Mobile menu should be visible on small screens");

        // Test tablet size
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(768, 1024)); // iPad size
        driver.navigate().refresh();

        WebElement tabletLayout = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.className("tablet-layout")));
        assertNotNull(tabletLayout, "Tablet layout should be visible on medium screens");
    }

    private void loginAndNavigateToDashboard() {
        String baseUrl = "http://localhost:" + port + "/spark-e.html";
        driver.get(baseUrl);

        WebElement loginForm = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("loginForm")));
        driver.findElement(By.id("email")).sendKeys("test@example.com");
        driver.findElement(By.id("password")).sendKeys("password123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for dashboard to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("dashboard")));
    }

    @org.junit.jupiter.api.AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
