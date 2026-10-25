package com.university.erp.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record PaymentRequest(
        UUID studentId,
        String feeType,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String paymentMethod,
        String transactionId,
        String receiptNumber,
        LocalDate paymentDate,
        String remarks,
        String createdBy
) {}
