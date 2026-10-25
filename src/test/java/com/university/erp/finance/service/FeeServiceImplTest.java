package com.university.erp.finance.service;

import com.university.erp.finance.dto.FeeRequest;
import com.university.erp.finance.dto.FeeResponse;
import com.university.erp.finance.entity.Fee;
import com.university.erp.finance.repository.FeeRepository;
import com.university.erp.finance.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeeServiceImplTest {

    @Mock
    private FeeRepository feeRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private FeeServiceImpl feeService;

    private Fee sampleFee;

    @BeforeEach
    void setUp() {
        sampleFee = Fee.builder()
                .id(1L)
                .feeType("TUITION")
                .amount(new BigDecimal("50000.00"))
                .description("Semester tuition fee")
                .active(true)
                .build();
    }

    @Test
    void testCreate_Success() {
        FeeRequest request = new FeeRequest("TUITION", new BigDecimal("50000.00"), "Semester tuition fee", true);
        when(feeRepository.existsByFeeTypeIgnoreCase("TUITION")).thenReturn(false);
        when(feeRepository.save(any(Fee.class))).thenReturn(sampleFee);

        FeeResponse response = feeService.create(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("TUITION", response.feeType());
        assertEquals(new BigDecimal("50000.00"), response.amount());
        assertTrue(response.active());
    }

    @Test
    void testCreate_DuplicateFeeType_ThrowsConflict() {
        FeeRequest request = new FeeRequest("TUITION", new BigDecimal("50000.00"), "Duplicate", true);
        when(feeRepository.existsByFeeTypeIgnoreCase("TUITION")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> feeService.create(request));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testCreate_InvalidAmount_ThrowsBadRequest() {
        FeeRequest request = new FeeRequest("TUITION", new BigDecimal("-100.00"), "Negative amount", true);
        when(feeRepository.existsByFeeTypeIgnoreCase("TUITION")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> feeService.create(request));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testGet_Success() {
        when(feeRepository.findById(1L)).thenReturn(Optional.of(sampleFee));

        FeeResponse response = feeService.get(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void testGet_NotFound() {
        when(feeRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> feeService.get(99L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testList_WithFilters() {
        when(feeRepository.findByFeeTypeContainingIgnoreCaseAndActive("TUITION", true))
                .thenReturn(List.of(sampleFee));

        List<FeeResponse> list = feeService.list("TUITION", true);

        assertEquals(1, list.size());
        assertEquals("TUITION", list.get(0).feeType());
    }

    @Test
    void testUpdate_Success() {
        FeeRequest updateRequest = new FeeRequest("TUITION_UPDATED", new BigDecimal("55000.00"), "Updated fee", false);
        when(feeRepository.findById(1L)).thenReturn(Optional.of(sampleFee));
        when(feeRepository.existsByFeeTypeIgnoreCaseAndIdNot("TUITION_UPDATED", 1L)).thenReturn(false);
        when(feeRepository.save(any(Fee.class))).thenAnswer(inv -> inv.getArgument(0));

        FeeResponse response = feeService.update(1L, updateRequest);

        assertNotNull(response);
        assertEquals("TUITION_UPDATED", response.feeType());
        assertEquals(new BigDecimal("55000.00"), response.amount());
        assertFalse(response.active());
    }

    @Test
    void testSetActive_Success() {
        when(feeRepository.findById(1L)).thenReturn(Optional.of(sampleFee));
        when(feeRepository.save(any(Fee.class))).thenAnswer(inv -> inv.getArgument(0));

        FeeResponse response = feeService.setActive(1L, false);

        assertNotNull(response);
        assertFalse(response.active());
    }

    @Test
    void testDelete_ReferencedByPayments_ThrowsConflict() {
        when(feeRepository.findById(1L)).thenReturn(Optional.of(sampleFee));
        when(paymentRepository.existsByFeeTypeIgnoreCase("TUITION")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> feeService.delete(1L));
        assertEquals(409, ex.getStatusCode().value());
        verify(feeRepository, never()).delete(any(Fee.class));
    }

    @Test
    void testDelete_NotReferenced_Success() {
        when(feeRepository.findById(1L)).thenReturn(Optional.of(sampleFee));
        when(paymentRepository.existsByFeeTypeIgnoreCase("TUITION")).thenReturn(false);

        feeService.delete(1L);

        verify(feeRepository, times(1)).delete(sampleFee);
    }
}
