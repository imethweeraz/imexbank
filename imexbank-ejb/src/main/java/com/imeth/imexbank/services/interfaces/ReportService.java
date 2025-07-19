package com.imeth.imexbank.services.interfaces;

import jakarta.ejb.Local;

import java.time.LocalDate;
import java.util.Map;

@Local
public interface ReportService {

    Map<String, Object> generateDailyTransactionReport(LocalDate date);

    Map<String, Object> generateMonthlyAccountSummary(int year, int month);

    Map<String, Object> generateCustomerReport(Long customerId);

    Map<String, Object> generateInterestReport(LocalDate startDate, LocalDate endDate);

    Map<String, Object> generateAuditReport(LocalDate startDate, LocalDate endDate);

    byte[] exportReportToPdf(Map<String, Object> reportData, String reportType);

    byte[] exportReportToExcel(Map<String, Object> reportData, String reportType);
}