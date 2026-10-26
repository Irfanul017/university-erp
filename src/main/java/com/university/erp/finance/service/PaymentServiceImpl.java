package com.university.erp.finance.service;

import com.university.erp.finance.dto.FinanceSummaryResponse;
import com.university.erp.finance.dto.PaymentRequest;
import com.university.erp.finance.dto.PaymentResponse;
import com.university.erp.finance.dto.ReceiptResponse;
import com.university.erp.finance.entity.Fee;
import com.university.erp.finance.entity.Payment;
import com.university.erp.finance.mapper.PaymentMapper;
import com.university.erp.finance.repository.FeeRepository;
import com.university.erp.finance.repository.PaymentRepository;
import com.university.erp.student.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              FeeRepository feeRepository,
                              StudentRepository studentRepository) {
        this.paymentRepository = paymentRepository;
        this.feeRepository = feeRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public PaymentResponse create(PaymentRequest request) {
        if (request == null) {
            throw badRequest("Request body must not be null");
        }
        if (request.studentId() == null) {
            throw badRequest("Student ID is required");
        }
        if (!studentRepository.existsById(request.studentId())) {
            throw notFound("Student not found with ID: " + request.studentId());
        }
        if (request.feeType() == null || request.feeType().trim().isEmpty()) {
            throw badRequest("Fee type is required");
        }
        String feeType = request.feeType().trim();

        BigDecimal totalAmount = request.totalAmount();
        if (totalAmount == null) {
            Optional<Fee> feeOpt = feeRepository.findByFeeTypeIgnoreCase(feeType);
            if (feeOpt.isPresent()) {
                totalAmount = feeOpt.get().getAmount();
            }
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw badRequest("Total amount must be greater than zero");
        }

        BigDecimal paidAmount = request.paidAmount() != null ? request.paidAmount() : BigDecimal.ZERO;
        if (paidAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw badRequest("Paid amount must not be negative");
        }
        if (paidAmount.compareTo(totalAmount) > 0) {
            throw badRequest("Paid amount cannot exceed total amount");
        }

        BigDecimal dueAmount = totalAmount.subtract(paidAmount);

        String paymentStatus;
        if (paidAmount.compareTo(totalAmount) >= 0) {
            paymentStatus = "PAID";
        } else if (paidAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = "PENDING";
        } else {
            paymentStatus = "PARTIAL";
        }

        String receiptNumber = request.receiptNumber();
        if (receiptNumber != null && !receiptNumber.trim().isEmpty()) {
            receiptNumber = receiptNumber.trim();
            if (paymentRepository.existsByReceiptNumber(receiptNumber)) {
                throw conflict("Receipt number '" + receiptNumber + "' already exists");
            }
        } else {
            receiptNumber = "REC-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }

        String transactionId = request.transactionId() != null && !request.transactionId().trim().isEmpty()
                ? request.transactionId().trim()
                : "TXN-DEMO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LocalDate paymentDate = request.paymentDate() != null ? request.paymentDate() : LocalDate.now();

        Payment payment = Payment.builder()
                .studentId(request.studentId())
                .feeType(feeType)
                .totalAmount(totalAmount)
                .paidAmount(paidAmount)
                .dueAmount(dueAmount)
                .paymentStatus(paymentStatus)
                .paymentMethod(request.paymentMethod() != null ? request.paymentMethod().trim() : "DEMO_CASH")
                .transactionId(transactionId)
                .receiptNumber(receiptNumber)
                .paymentDate(paymentDate)
                .remarks(request.remarks() != null ? request.remarks().trim() : null)
                .createdBy(request.createdBy() != null ? request.createdBy().trim() : "ADMIN")
                .build();

        return PaymentMapper.toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse get(UUID paymentId) {
        return PaymentMapper.toResponse(findPayment(paymentId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> list(String feeType, String paymentStatus, LocalDate paymentDate, UUID studentId) {
        String cleanType = (feeType != null && !feeType.trim().isEmpty()) ? feeType.trim() : null;
        String cleanStatus = (paymentStatus != null && !paymentStatus.trim().isEmpty()) ? paymentStatus.trim() : null;

        return paymentRepository.findAll().stream()
                .filter(p -> cleanType == null || (p.getFeeType() != null && p.getFeeType().equalsIgnoreCase(cleanType)))
                .filter(p -> cleanStatus == null || (p.getPaymentStatus() != null && p.getPaymentStatus().equalsIgnoreCase(cleanStatus)))
                .filter(p -> paymentDate == null || (p.getPaymentDate() != null && p.getPaymentDate().equals(paymentDate)))
                .filter(p -> studentId == null || (p.getStudentId() != null && p.getStudentId().equals(studentId)))
                .sorted(Comparator.comparing(Payment::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getByStudentId(UUID studentId) {
        if (studentId == null) {
            throw badRequest("Student ID must not be null");
        }
        if (!studentRepository.existsById(studentId)) {
            throw notFound("Student not found with ID: " + studentId);
        }
        return paymentRepository.findByStudentId(studentId)
                .stream()
                .map(PaymentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponse getReceipt(UUID paymentId) {
        Payment payment = findPayment(paymentId);
        if ("PENDING".equalsIgnoreCase(payment.getPaymentStatus()) || "FAILED".equalsIgnoreCase(payment.getPaymentStatus())) {
            throw badRequest("Cannot issue a payment receipt for transaction with status: " + payment.getPaymentStatus());
        }
        return PaymentMapper.toReceiptResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public FinanceSummaryResponse getSummary() {
        List<Fee> activeFees = feeRepository.findByActive(true);
        long activeCount = activeFees.size();
        BigDecimal totalConfiguredFeeAmount = activeFees.stream()
                .map(Fee::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Payment> allPayments = paymentRepository.findAll();

        BigDecimal totalPaid = allPayments.stream()
                .map(Payment::getPaidAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = allPayments.stream()
                .map(Payment::getDueAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> statusCounts = allPayments.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getPaymentStatus() != null ? p.getPaymentStatus() : "UNKNOWN",
                        Collectors.counting()
                ));

        return new FinanceSummaryResponse(
                activeCount,
                totalConfiguredFeeAmount,
                totalPaid,
                totalOutstanding,
                statusCounts
        );
    }

    private Payment findPayment(UUID paymentId) {
        if (paymentId == null) {
            throw badRequest("Payment ID must not be null");
        }
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> notFound("Payment record not found with ID: " + paymentId));
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
