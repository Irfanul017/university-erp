package com.university.erp.finance.controller;

import com.university.erp.finance.dto.FinanceSummaryResponse;
import com.university.erp.finance.dto.PaymentRequest;
import com.university.erp.finance.dto.PaymentResponse;
import com.university.erp.finance.dto.ReceiptResponse;
import com.university.erp.finance.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private UUID studentId;
    private UUID paymentId;
    private PaymentResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
        studentId = UUID.randomUUID();
        paymentId = UUID.randomUUID();
        sampleResponse = new PaymentResponse(
                paymentId, studentId, "TUITION", new BigDecimal("50000.00"), new BigDecimal("50000.00"),
                BigDecimal.ZERO, "PAID", "BANK_TRANSFER", "TXN-12345", "REC-001",
                LocalDate.now(), "Full payment", LocalDateTime.now(), LocalDateTime.now(), "ADMIN", null
        );
    }

    @Test
    void testCreatePayment() throws Exception {
        when(paymentService.create(any(PaymentRequest.class))).thenReturn(sampleResponse);

        String json = """
                {
                    "studentId": "%s",
                    "feeType": "TUITION",
                    "totalAmount": 50000.00,
                    "paidAmount": 50000.00,
                    "paymentMethod": "BANK_TRANSFER"
                }
                """.formatted(studentId);

        mockMvc.perform(post("/api/payments").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"))
                .andExpect(jsonPath("$.dueAmount").value(0));
    }

    @Test
    void testGetPayment() throws Exception {
        when(paymentService.get(paymentId)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/payments/" + paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentId").value(paymentId.toString()))
                .andExpect(jsonPath("$.feeType").value("TUITION"));
    }

    @Test
    void testListPayments() throws Exception {
        when(paymentService.list(isNull(), isNull(), isNull(), isNull())).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetByStudentId() throws Exception {
        when(paymentService.getByStudentId(studentId)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/payments/student/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentId").value(studentId.toString()));
    }

    @Test
    void testGetReceipt() throws Exception {
        ReceiptResponse receiptResponse = new ReceiptResponse(
                "REC-001", paymentId, studentId, "TUITION", new BigDecimal("50000.00"),
                new BigDecimal("50000.00"), BigDecimal.ZERO, "PAID", "BANK_TRANSFER",
                "TXN-12345", LocalDate.now(), "Full payment", LocalDateTime.now()
        );
        when(paymentService.getReceipt(paymentId)).thenReturn(receiptResponse);

        mockMvc.perform(get("/api/payments/" + paymentId + "/receipt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.receiptNumber").value("REC-001"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    void testGetSummary() throws Exception {
        FinanceSummaryResponse summaryResponse = new FinanceSummaryResponse(
                3L, new BigDecimal("150000.00"), new BigDecimal("100000.00"),
                new BigDecimal("50000.00"), Map.of("PAID", 2L, "PARTIAL", 1L)
        );
        when(paymentService.getSummary()).thenReturn(summaryResponse);

        mockMvc.perform(get("/api/payments/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalActiveFeeDefinitions").value(3))
                .andExpect(jsonPath("$.totalRecordedPaidAmount").value(100000.00));
    }
}
