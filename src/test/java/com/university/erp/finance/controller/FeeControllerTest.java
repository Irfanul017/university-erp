package com.university.erp.finance.controller;

import com.university.erp.finance.dto.FeeRequest;
import com.university.erp.finance.dto.FeeResponse;
import com.university.erp.finance.service.FeeService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FeeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FeeService feeService;

    @InjectMocks
    private FeeController feeController;

    private FeeResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(feeController).build();
        sampleResponse = new FeeResponse(1L, "TUITION", new BigDecimal("50000.00"), "Semester fee", true);
    }

    @Test
    void testCreateFee() throws Exception {
        when(feeService.create(any(FeeRequest.class))).thenReturn(sampleResponse);

        String json = """
                {
                    "feeType": "TUITION",
                    "amount": 50000.00,
                    "description": "Semester fee",
                    "active": true
                }
                """;

        mockMvc.perform(post("/api/fees").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.feeType").value("TUITION"))
                .andExpect(jsonPath("$.amount").value(50000.00))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testGetFee() throws Exception {
        when(feeService.get(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/fees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.feeType").value("TUITION"));
    }

    @Test
    void testListFees() throws Exception {
        when(feeService.list(eq("TUITION"), eq(true))).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/fees?feeType=TUITION&active=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].feeType").value("TUITION"));
    }

    @Test
    void testUpdateFee() throws Exception {
        FeeResponse updated = new FeeResponse(1L, "TUITION_NEW", new BigDecimal("55000.00"), "Updated fee", true);
        when(feeService.update(eq(1L), any(FeeRequest.class))).thenReturn(updated);

        String json = """
                {
                    "feeType": "TUITION_NEW",
                    "amount": 55000.00,
                    "description": "Updated fee",
                    "active": true
                }
                """;

        mockMvc.perform(put("/api/fees/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feeType").value("TUITION_NEW"))
                .andExpect(jsonPath("$.amount").value(55000.00));
    }

    @Test
    void testSetActive() throws Exception {
        FeeResponse deactivated = new FeeResponse(1L, "TUITION", new BigDecimal("50000.00"), "Semester fee", false);
        when(feeService.setActive(eq(1L), eq(false))).thenReturn(deactivated);

        mockMvc.perform(patch("/api/fees/1/active?active=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void testDeleteFee() throws Exception {
        doNothing().when(feeService).delete(1L);

        mockMvc.perform(delete("/api/fees/1"))
                .andExpect(status().isNoContent());
    }
}
