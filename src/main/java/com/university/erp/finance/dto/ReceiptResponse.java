package com.university.erp.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReceiptResponse(
        String receiptNumber,
        UUID paymentId,
        UUID studentId,
        String feeType,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        BigDecimal dueAmount,
        String paymentStatus,
        String paymentMethod,
        String transactionId,
        LocalDate paymentDate,
        String remarks,
        LocalDateTime generatedAt
) {}
