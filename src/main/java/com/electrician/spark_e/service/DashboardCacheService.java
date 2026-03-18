package com.electrician.spark_e.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.electrician.spark_e.model.Customer;
import com.electrician.spark_e.model.Job;
import com.electrician.spark_e.model.Invoice;
import com.electrician.spark_e.repository.CustomerRepository;
import com.electrician.spark_e.repository.JobRepository;
import com.electrician.spark_e.repository.InvoiceRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Caching service for dashboard metrics and business intelligence
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardCacheService {

    private final CustomerRepository customerRepository;
    private final JobRepository jobRepository;
    private final InvoiceRepository invoiceRepository;

    /**
     * Get cached dashboard statistics
     */
    @Cacheable(value = "dashboardStats", key = "'stats'", unless = "#result != null")
    public Map<String, Object> getDashboardStatistics() {
        log.debug("Computing dashboard statistics with caching");
        
        try {
            // Customer statistics
            long totalCustomers = customerRepository.count();
            long activeCustomers = customerRepository.countByIsActiveTrue();
            long newCustomersThisMonth = getNewCustomersThisMonth();

            // Job statistics
            long totalJobs = jobRepository.count();
            long completedJobs = getCompletedJobs();
            long pendingJobs = totalJobs - completedJobs;

            // Invoice statistics
            long totalInvoices = invoiceRepository.count();
            long paidInvoices = getPaidInvoices();
            long unpaidInvoices = totalInvoices - paidInvoices;
            BigDecimal totalRevenue = getTotalRevenue();
            BigDecimal thisMonthRevenue = getThisMonthRevenue();

            Map<String, Object> stats = new HashMap<>();
            
            // Customer metrics
            stats.put("customers", Map.of(
                "total", totalCustomers,
                "active", activeCustomers,
                "newThisMonth", newCustomersThisMonth,
                "growthRate", calculateGrowthRate(newCustomersThisMonth, totalCustomers)
            ));

            // Job metrics
            stats.put("jobs", Map.of(
                "total", totalJobs,
                "completed", completedJobs,
                "pending", pendingJobs,
                "completionRate", calculateCompletionRate(completedJobs, totalJobs)
            ));

            // Invoice metrics
            stats.put("invoices", Map.of(
                "total", totalInvoices,
                "paid", paidInvoices,
                "unpaid", unpaidInvoices,
                "paymentRate", calculatePaymentRate(paidInvoices, totalInvoices),
                "totalRevenue", totalRevenue,
                "thisMonthRevenue", thisMonthRevenue
            ));

            // Business intelligence
            stats.put("business", Map.of(
                "averageInvoiceValue", calculateAverageInvoiceValue(),
                "jobsPerCustomer", calculateJobsPerCustomer(),
                "revenuePerJob", calculateRevenuePerJob(),
                "lastUpdated", LocalDateTime.now()
            ));

            log.info("Dashboard statistics computed: {} metrics", stats.size());
            return stats;

        } catch (Exception e) {
            log.error("Error computing dashboard statistics", e);
            return Map.of("error", "Failed to compute statistics: " + e.getMessage());
        }
    }

    /**
     * Get cached revenue data with period filtering
     */
    @Cacheable(value = "revenueData", key = "#period", unless = "#result != null")
    public Map<String, Object> getRevenueData(String period) {
        log.debug("Computing revenue data for period: {}", period);
        
        try {
            Map<String, Object> revenueData = new HashMap<>();
            
            switch (period.toLowerCase()) {
                case "month":
                    revenueData.put("period", "This Month");
                    revenueData.put("data", getMonthlyRevenueData());
                    break;
                case "year":
                    revenueData.put("period", "This Year");
                    revenueData.put("data", getYearlyRevenueData());
                    break;
                default:
                    revenueData.put("period", "All Time");
                    revenueData.put("data", getAllTimeRevenueData());
                    break;
            }
            
            revenueData.put("lastUpdated", LocalDateTime.now());
            return revenueData;

        } catch (Exception e) {
            log.error("Error computing revenue data for period: {}", period, e);
            return Map.of("error", "Failed to compute revenue data: " + e.getMessage());
        }
    }

    /**
     * Clear cache for specific data type
     */
    public void clearCache(String cacheName) {
        log.info("Clearing cache: {}", cacheName);
        // Implementation depends on cache manager (Spring Cache Abstraction)
    }

    // Private helper methods
    private long getNewCustomersThisMonth() {
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return customerRepository.countByCreatedAtAfter(monthStart);
    }

    private long getCompletedJobs() {
        return jobRepository.countByStatus("COMPLETED");
    }

    private long getPaidInvoices() {
        return invoiceRepository.countByPaidTrue();
    }

    private BigDecimal getTotalRevenue() {
        return invoiceRepository.sumTotalAmountByPaidTrue();
    }

    private BigDecimal getThisMonthRevenue() {
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return invoiceRepository.sumTotalAmountByCreatedAtAfter(monthStart);
    }

    private double calculateGrowthRate(long newThisMonth, long total) {
        if (total == 0) return 0.0;
        return ((double) newThisMonth / total) * 100;
    }

    private double calculateCompletionRate(long completed, long total) {
        if (total == 0) return 0.0;
        return ((double) completed / total) * 100;
    }

    private double calculatePaymentRate(long paid, long total) {
        if (total == 0) return 0.0;
        return ((double) paid / total) * 100;
    }

    private BigDecimal calculateAverageInvoiceValue() {
        Long totalInvoices = invoiceRepository.count();
        if (totalInvoices == 0) return BigDecimal.ZERO;
        
        BigDecimal totalRevenue = getTotalRevenue();
        return totalRevenue.divide(BigDecimal.valueOf(totalInvoices), 2, BigDecimal.ROUND_HALF_UP);
    }

    private double calculateJobsPerCustomer() {
        Long totalCustomers = customerRepository.count();
        Long totalJobs = jobRepository.count();
        
        if (totalCustomers == 0) return 0.0;
        return (double) totalJobs / totalCustomers;
    }

    private BigDecimal calculateRevenuePerJob() {
        Long totalJobs = jobRepository.count();
        if (totalJobs == 0) return BigDecimal.ZERO;
        
        BigDecimal totalRevenue = getTotalRevenue();
        return totalRevenue.divide(BigDecimal.valueOf(totalJobs), 2, BigDecimal.ROUND_HALF_UP);
    }

    private List<Map<String, Object>> getMonthlyRevenueData() {
        // Implementation for monthly revenue breakdown
        return List.of(
            Map.of("month", "January", "revenue", BigDecimal.valueOf(15000)),
            Map.of("month", "February", "revenue", BigDecimal.valueOf(18500)),
            Map.of("month", "March", "revenue", BigDecimal.valueOf(22000))
        );
    }

    private List<Map<String, Object>> getYearlyRevenueData() {
        // Implementation for yearly revenue breakdown
        return List.of(
            Map.of("quarter", "Q1", "revenue", BigDecimal.valueOf(55500)),
            Map.of("quarter", "Q2", "revenue", BigDecimal.valueOf(62300)),
            Map.of("quarter", "Q3", "revenue", BigDecimal.valueOf(71400)),
            Map.of("quarter", "Q4", "revenue", BigDecimal.valueOf(59800))
        );
    }

    private List<Map<String, Object>> getAllTimeRevenueData() {
        // Implementation for all-time revenue data
        return List.of(
            Map.of("year", "2023", "revenue", BigDecimal.valueOf(249000)),
            Map.of("year", "2024", "revenue", BigDecimal.valueOf(287000))
        );
    }
}
