package com.university.erp.finance.dto;

import java.math.BigDecimal;
import java.util.Map;

public record FinanceSummaryResponse(
        long totalActiveFeeDefinitions,
        BigDecimal totalConfiguredFeeAmount,
        BigDecimal totalRecordedPaidAmount,
        BigDecimal totalOutstandingAmount,
        Map<String, Long> paymentsByStatus
) {}
