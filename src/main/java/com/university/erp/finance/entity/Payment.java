package com.university.erp.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "fee_payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id")
    private UUID id;
    @Column(name = "student_id", nullable = false)
    private UUID studentId;
    @Column(name = "fee_type", nullable = false)
    private String feeType;
    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;
    @Column(name = "paid_amount")
    private BigDecimal paidAmount = BigDecimal.ZERO;
    @Column(name = "due_amount")
    private BigDecimal dueAmount;
    @Column(name = "payment_status")
    private String paymentStatus = "PENDING";
    @Column(name = "payment_method")
    private String paymentMethod;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "receipt_number", unique = true)
    private String receiptNumber;
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    @Column(columnDefinition = "text")
    private String remarks;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    void create() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void update() {
        updatedAt = LocalDateTime.now();
    }
}
