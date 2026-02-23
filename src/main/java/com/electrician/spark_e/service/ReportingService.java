package com.electrician.spark_e.service;

import com.electrician.spark_e.model.*;
import com.electrician.spark_e.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportingService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ElectricianRepository electricianRepository;

    // 1. Electrician performance report
    public ElectricianPerformanceReport getElectricianPerformance(Long electricianId, YearMonth month) {
        LocalDate start = month.atDay(1);
        LocalDate end = month.atEndOfMonth();

        List<Job> jobs = jobRepository.findByElectricianIdAndScheduledDateBetween(electricianId, start.atStartOfDay(), end.atTime(23, 59, 59));
        List<Invoice> invoices = invoiceRepository.findByJobElectricianIdAndIssueDateBetween(electricianId, start, end);

        ElectricianPerformanceReport report = new ElectricianPerformanceReport();
        report.setElectricianId(electricianId);
        report.setMonth(month);
        report.setTotalJobs(jobs.size());
        report.setCompletedJobs(jobs.stream().filter(j -> j.getStatus() == JobStatus.COMPLETED).count());
        report.setTotalInvoiced(invoices.stream().mapToDouble(Invoice::getTotalAmount).sum());
        report.setAverageJobValue(invoices.stream().mapToDouble(Invoice::getTotalAmount).average().orElse(0.0));

        return report;
    }

    // 2. Inventory report – low stock parts
    public InventoryReport getInventoryReport() {
        List<Part> lowStockParts = partRepository.findByStockQuantityLessThanEqual(5); // threshold can be parameterized
        List<PartUsage> mostUsedParts = getMostUsedParts(10); // top 10

        InventoryReport report = new InventoryReport();
        report.setLowStockParts(lowStockParts);
        report.setMostUsedParts(mostUsedParts);
        return report;
    }

    private List<PartUsage> getMostUsedParts(int limit) {
        // This would require a custom query; for simplicity, we can implement a native query in PartUsedRepository
        // We'll assume we have a method in PartUsedRepository: List<Object[]> findMostUsedParts(Pageable pageable)
        // Then transform to PartUsage DTO. For now, return empty list.
        return List.of();
    }

    // 3. Customer profitability report
    public CustomerProfitabilityReport getCustomerProfitability(Long customerId) {
        List<Invoice> invoices = invoiceRepository.findByJobCustomerId(customerId);

        CustomerProfitabilityReport report = new CustomerProfitabilityReport();
        report.setCustomerId(customerId);
        report.setTotalJobs(invoices.size());
        report.setTotalRevenue(invoices.stream().mapToDouble(Invoice::getTotalAmount).sum());
        report.setAverageInvoiceValue(invoices.stream().mapToDouble(Invoice::getTotalAmount).average().orElse(0.0));
        report.setPaymentTimeAverage(calculateAveragePaymentTime(invoices));

        return report;
    }

    private Double calculateAveragePaymentTime(List<Invoice> invoices) {
        return invoices.stream()
                .filter(i -> i.getPaid() && i.getPaidDate() != null)
                .mapToLong(i -> java.time.temporal.ChronoUnit.DAYS.between(i.getIssueDate(), i.getPaidDate()))
                .average()
                .orElse(0.0);
    }

    // DTOs (inner classes)
    public static class ElectricianPerformanceReport {
        private Long electricianId;
        private YearMonth month;
        private long totalJobs;
        private long completedJobs;
        private double totalInvoiced;
        private double averageJobValue;
        // getters/setters (generate using IntelliJ)
        public Long getElectricianId() { return electricianId; }
        public void setElectricianId(Long electricianId) { this.electricianId = electricianId; }
        public YearMonth getMonth() { return month; }
        public void setMonth(YearMonth month) { this.month = month; }
        public long getTotalJobs() { return totalJobs; }
        public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }
        public long getCompletedJobs() { return completedJobs; }
        public void setCompletedJobs(long completedJobs) { this.completedJobs = completedJobs; }
        public double getTotalInvoiced() { return totalInvoiced; }
        public void setTotalInvoiced(double totalInvoiced) { this.totalInvoiced = totalInvoiced; }
        public double getAverageJobValue() { return averageJobValue; }
        public void setAverageJobValue(double averageJobValue) { this.averageJobValue = averageJobValue; }
    }

    public static class InventoryReport {
        private List<Part> lowStockParts;
        private List<PartUsage> mostUsedParts;
        // getters/setters
        public List<Part> getLowStockParts() { return lowStockParts; }
        public void setLowStockParts(List<Part> lowStockParts) { this.lowStockParts = lowStockParts; }
        public List<PartUsage> getMostUsedParts() { return mostUsedParts; }
        public void setMostUsedParts(List<PartUsage> mostUsedParts) { this.mostUsedParts = mostUsedParts; }
    }

    public static class PartUsage {
        private String partName;
        private Long totalQuantity;
        private Double totalValue;
        // getters/setters
        public String getPartName() { return partName; }
        public void setPartName(String partName) { this.partName = partName; }
        public Long getTotalQuantity() { return totalQuantity; }
        public void setTotalQuantity(Long totalQuantity) { this.totalQuantity = totalQuantity; }
        public Double getTotalValue() { return totalValue; }
        public void setTotalValue(Double totalValue) { this.totalValue = totalValue; }
    }

    public static class CustomerProfitabilityReport {
        private Long customerId;
        private long totalJobs;
        private double totalRevenue;
        private double averageInvoiceValue;
        private double paymentTimeAverage; // days
        // getters/setters
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public long getTotalJobs() { return totalJobs; }
        public void setTotalJobs(long totalJobs) { this.totalJobs = totalJobs; }
        public double getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
        public double getAverageInvoiceValue() { return averageInvoiceValue; }
        public void setAverageInvoiceValue(double averageInvoiceValue) { this.averageInvoiceValue = averageInvoiceValue; }
        public double getPaymentTimeAverage() { return paymentTimeAverage; }
        public void setPaymentTimeAverage(double paymentTimeAverage) { this.paymentTimeAverage = paymentTimeAverage; }
    }
}