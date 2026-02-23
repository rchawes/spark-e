package com.electrician.spark_e.controller;

import com.electrician.spark_e.service.ReportingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportingService reportingService;

    @GetMapping("/electrician/{electricianId}")
    public ResponseEntity<?> getElectricianPerformance(
            @PathVariable Long electricianId,
            @RequestParam int year,
            @RequestParam int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        ReportingService.ElectricianPerformanceReport report =
                reportingService.getElectricianPerformance(electricianId, yearMonth);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/inventory")
    public ResponseEntity<?> getInventoryReport() {
        return ResponseEntity.ok(reportingService.getInventoryReport());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getCustomerProfitability(@PathVariable Long customerId) {
        return ResponseEntity.ok(reportingService.getCustomerProfitability(customerId));
    }
}