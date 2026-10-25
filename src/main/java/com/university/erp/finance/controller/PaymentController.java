package com.university.erp.finance.controller;

import com.university.erp.finance.dto.FinanceSummaryResponse;
import com.university.erp.finance.dto.PaymentRequest;
import com.university.erp.finance.dto.PaymentResponse;
import com.university.erp.finance.dto.ReceiptResponse;
import com.university.erp.finance.service.PaymentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(request));
    }

    @GetMapping("/{paymentId}")
    public PaymentResponse get(@PathVariable UUID paymentId) {
        return paymentService.get(paymentId);
    }

    @GetMapping
    public List<PaymentResponse> list(
            @RequestParam(required = false) String feeType,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate paymentDate,
            @RequestParam(required = false) UUID studentId) {
        return paymentService.list(feeType, paymentStatus, paymentDate, studentId);
    }

    @GetMapping("/student/{studentId}")
    public List<PaymentResponse> getByStudentId(@PathVariable UUID studentId) {
        return paymentService.getByStudentId(studentId);
    }

    @GetMapping("/{paymentId}/receipt")
    public ReceiptResponse getReceipt(@PathVariable UUID paymentId) {
        return paymentService.getReceipt(paymentId);
    }

    @GetMapping("/summary")
    public FinanceSummaryResponse getSummary() {
        return paymentService.getSummary();
    }
}
