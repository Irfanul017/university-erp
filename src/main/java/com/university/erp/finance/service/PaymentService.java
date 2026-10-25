package com.university.erp.finance.service;

import com.university.erp.finance.dto.FinanceSummaryResponse;
import com.university.erp.finance.dto.PaymentRequest;
import com.university.erp.finance.dto.PaymentResponse;
import com.university.erp.finance.dto.ReceiptResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse create(PaymentRequest request);

    PaymentResponse get(UUID paymentId);

    List<PaymentResponse> list(String feeType, String paymentStatus, LocalDate paymentDate, UUID studentId);

    List<PaymentResponse> getByStudentId(UUID studentId);

    ReceiptResponse getReceipt(UUID paymentId);

    FinanceSummaryResponse getSummary();
}
