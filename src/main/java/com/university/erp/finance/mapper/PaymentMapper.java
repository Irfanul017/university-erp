package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.PaymentRequest;
import com.university.erp.finance.dto.PaymentResponse;
import com.university.erp.finance.dto.ReceiptResponse;
import com.university.erp.finance.entity.Payment;

import java.time.LocalDateTime;

public final class PaymentMapper {

    private PaymentMapper() {
    }

    public static Payment toEntity(PaymentRequest request) {
        if (request == null) {
            return null;
        }
        return Payment.builder()
                .studentId(request.studentId())
                .feeType(request.feeType() != null ? request.feeType().trim() : null)
                .totalAmount(request.totalAmount())
                .paidAmount(request.paidAmount())
                .paymentMethod(request.paymentMethod() != null ? request.paymentMethod().trim() : null)
                .transactionId(request.transactionId() != null ? request.transactionId().trim() : null)
                .receiptNumber(request.receiptNumber() != null ? request.receiptNumber().trim() : null)
                .paymentDate(request.paymentDate())
                .remarks(request.remarks() != null ? request.remarks().trim() : null)
                .createdBy(request.createdBy() != null ? request.createdBy().trim() : null)
                .build();
    }

    public static PaymentResponse toResponse(Payment entity) {
        if (entity == null) {
            return null;
        }
        return new PaymentResponse(
                entity.getId(),
                entity.getStudentId(),
                entity.getFeeType(),
                entity.getTotalAmount(),
                entity.getPaidAmount(),
                entity.getDueAmount(),
                entity.getPaymentStatus(),
                entity.getPaymentMethod(),
                entity.getTransactionId(),
                entity.getReceiptNumber(),
                entity.getPaymentDate(),
                entity.getRemarks(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedBy()
        );
    }

    public static ReceiptResponse toReceiptResponse(Payment entity) {
        if (entity == null) {
            return null;
        }
        return new ReceiptResponse(
                entity.getReceiptNumber(),
                entity.getId(),
                entity.getStudentId(),
                entity.getFeeType(),
                entity.getTotalAmount(),
                entity.getPaidAmount(),
                entity.getDueAmount(),
                entity.getPaymentStatus(),
                entity.getPaymentMethod(),
                entity.getTransactionId(),
                entity.getPaymentDate(),
                entity.getRemarks(),
                LocalDateTime.now()
        );
    }
}
