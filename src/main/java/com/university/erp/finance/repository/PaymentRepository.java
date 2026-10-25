package com.university.erp.finance.repository;

import com.university.erp.finance.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByStudentId(UUID studentId);

    List<Payment> findByFeeTypeIgnoreCase(String feeType);

    List<Payment> findByPaymentStatusIgnoreCase(String paymentStatus);

    Optional<Payment> findByReceiptNumber(String receiptNumber);

    boolean existsByReceiptNumber(String receiptNumber);

    boolean existsByReceiptNumberAndIdNot(String receiptNumber, UUID id);

    boolean existsByFeeTypeIgnoreCase(String feeType);

    @Query("SELECT p FROM Payment p WHERE " +
            "(:feeType IS NULL OR LOWER(p.feeType) = LOWER(:feeType)) AND " +
            "(:status IS NULL OR LOWER(p.paymentStatus) = LOWER(:status)) AND " +
            "(:paymentDate IS NULL OR p.paymentDate = :paymentDate) AND " +
            "(:studentId IS NULL OR p.studentId = :studentId) " +
            "ORDER BY p.createdAt DESC")
    List<Payment> searchPayments(
            @Param("feeType") String feeType,
            @Param("status") String status,
            @Param("paymentDate") LocalDate paymentDate,
            @Param("studentId") UUID studentId);
}
