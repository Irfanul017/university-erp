package com.university.erp.finance.service;

import com.university.erp.finance.dto.FinanceSummaryResponse;
import com.university.erp.finance.dto.PaymentRequest;
import com.university.erp.finance.dto.PaymentResponse;
import com.university.erp.finance.dto.ReceiptResponse;
import com.university.erp.finance.entity.Fee;
import com.university.erp.finance.entity.Payment;
import com.university.erp.finance.repository.FeeRepository;
import com.university.erp.finance.repository.PaymentRepository;
import com.university.erp.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private FeeRepository feeRepository;

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private UUID studentId;
    private UUID paymentId;
    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        studentId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
        samplePayment = Payment.builder()
                .id(paymentId)
                .studentId(studentId)
                .feeType("TUITION")
                .totalAmount(new BigDecimal("50000.00"))
                .paidAmount(new BigDecimal("50000.00"))
                .dueAmount(BigDecimal.ZERO)
                .paymentStatus("PAID")
                .paymentMethod("BANK_TRANSFER")
                .transactionId("TXN-12345")
                .receiptNumber("REC-001")
                .paymentDate(LocalDate.now())
                .createdBy("ADMIN")
                .build();
    }

    @Test
    void testCreate_Success_PaidInFull() {
        PaymentRequest request = new PaymentRequest(
                studentId, "TUITION", new BigDecimal("50000.00"), new BigDecimal("50000.00"),
                "BANK_TRANSFER", "TXN-12345", "REC-001", LocalDate.now(), "Full payment", "ADMIN"
        );

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(paymentRepository.existsByReceiptNumber("REC-001")).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);

        PaymentResponse response = paymentService.create(request);

        assertNotNull(response);
        assertEquals("PAID", response.paymentStatus());
        assertEquals(BigDecimal.ZERO, response.dueAmount());
        assertEquals("REC-001", response.receiptNumber());
    }

    @Test
    void testCreate_PartialPayment() {
        PaymentRequest request = new PaymentRequest(
                studentId, "TUITION", new BigDecimal("50000.00"), new BigDecimal("20000.00"),
                "CASH", null, null, LocalDate.now(), "Partial", "ADMIN"
        );

        Payment partialPayment = Payment.builder()
                .id(paymentId)
                .studentId(studentId)
                .feeType("TUITION")
                .totalAmount(new BigDecimal("50000.00"))
                .paidAmount(new BigDecimal("20000.00"))
                .dueAmount(new BigDecimal("30000.00"))
                .paymentStatus("PARTIAL")
                .receiptNumber("REC-AUTO-123")
                .build();

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(paymentRepository.save(any(Payment.class))).thenReturn(partialPayment);

        PaymentResponse response = paymentService.create(request);

        assertNotNull(response);
        assertEquals("PARTIAL", response.paymentStatus());
        assertEquals(new BigDecimal("30000.00"), response.dueAmount());
    }

    @Test
    void testCreate_StudentNotFound_ThrowsNotFound() {
        PaymentRequest request = new PaymentRequest(
                studentId, "TUITION", new BigDecimal("50000.00"), new BigDecimal("50000.00"),
                "CASH", null, null, null, null, null
        );

        when(studentRepository.existsById(studentId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> paymentService.create(request));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testCreate_NegativePaidAmount_ThrowsBadRequest() {
        PaymentRequest request = new PaymentRequest(
                studentId, "TUITION", new BigDecimal("50000.00"), new BigDecimal("-100.00"),
                "CASH", null, null, null, null, null
        );

        when(studentRepository.existsById(studentId)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> paymentService.create(request));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testCreate_PaidExceedsTotal_ThrowsBadRequest() {
        PaymentRequest request = new PaymentRequest(
                studentId, "TUITION", new BigDecimal("50000.00"), new BigDecimal("60000.00"),
                "CASH", null, null, null, null, null
        );

        when(studentRepository.existsById(studentId)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> paymentService.create(request));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testCreate_DuplicateReceiptNumber_ThrowsConflict() {
        PaymentRequest request = new PaymentRequest(
                studentId, "TUITION", new BigDecimal("50000.00"), new BigDecimal("50000.00"),
                "CASH", null, "REC-001", null, null, null
        );

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(paymentRepository.existsByReceiptNumber("REC-001")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> paymentService.create(request));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testGetReceipt_Success() {
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(samplePayment));

        ReceiptResponse receipt = paymentService.getReceipt(paymentId);

        assertNotNull(receipt);
        assertEquals("REC-001", receipt.receiptNumber());
        assertEquals(studentId, receipt.studentId());
        assertEquals(new BigDecimal("50000.00"), receipt.paidAmount());
    }

    @Test
    void testGetReceipt_PendingPayment_ThrowsBadRequest() {
        samplePayment.setPaymentStatus("PENDING");
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(samplePayment));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> paymentService.getReceipt(paymentId));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testGetSummary() {
        Fee activeFee = Fee.builder().id(1L).amount(new BigDecimal("50000.00")).active(true).build();
        when(feeRepository.findByActive(true)).thenReturn(List.of(activeFee));
        when(paymentRepository.findAll()).thenReturn(List.of(samplePayment));

        FinanceSummaryResponse summary = paymentService.getSummary();

        assertNotNull(summary);
        assertEquals(1, summary.totalActiveFeeDefinitions());
        assertEquals(new BigDecimal("50000.00"), summary.totalConfiguredFeeAmount());
        assertEquals(new BigDecimal("50000.00"), summary.totalRecordedPaidAmount());
        assertEquals(1, summary.paymentsByStatus().get("PAID"));
    }
}
