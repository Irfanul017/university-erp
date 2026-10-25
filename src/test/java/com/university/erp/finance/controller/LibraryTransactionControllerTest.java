package com.university.erp.finance.controller;

import com.university.erp.finance.dto.LibraryTransactionRequest;
import com.university.erp.finance.dto.LibraryTransactionResponse;
import com.university.erp.finance.dto.LibraryTransactionReturnRequest;
import com.university.erp.finance.service.LibraryTransactionService;
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
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LibraryTransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LibraryTransactionService libraryTransactionService;

    @InjectMocks
    private LibraryTransactionController libraryTransactionController;

    private UUID studentId;
    private LibraryTransactionResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(libraryTransactionController).build();
        studentId = UUID.randomUUID();
        sampleResponse = new LibraryTransactionResponse(
                100L, 1L, "Clean Architecture", "978-0134494166",
                studentId, LocalDate.now(), LocalDate.now().plusDays(14), null,
                "ISSUED", 0, BigDecimal.ZERO
        );
    }

    @Test
    void testIssueBook() throws Exception {
        when(libraryTransactionService.issueBook(any(LibraryTransactionRequest.class))).thenReturn(sampleResponse);

        String json = """
                {
                    "bookId": 1,
                    "studentId": "%s",
                    "issueDate": "2026-10-25",
                    "dueDate": "2026-11-08"
                }
                """.formatted(studentId);

        mockMvc.perform(post("/api/library/transactions").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.bookTitle").value("Clean Architecture"))
                .andExpect(jsonPath("$.status").value("ISSUED"));
    }

    @Test
    void testReturnBook() throws Exception {
        LibraryTransactionResponse returnedResponse = new LibraryTransactionResponse(
                100L, 1L, "Clean Architecture", "978-0134494166",
                studentId, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), LocalDate.now(),
                "RETURNED", 6, new BigDecimal("30.00")
        );
        when(libraryTransactionService.returnBook(eq(100L), any())).thenReturn(returnedResponse);

        String json = """
                {
                    "returnDate": "2026-10-25"
                }
                """;

        mockMvc.perform(post("/api/library/transactions/100/return").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.overdueDays").value(6))
                .andExpect(jsonPath("$.fineAmount").value(30.00));
    }

    @Test
    void testGetTransaction() throws Exception {
        when(libraryTransactionService.get(100L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/library/transactions/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.bookTitle").value("Clean Architecture"));
    }

    @Test
    void testListTransactions() throws Exception {
        when(libraryTransactionService.list(isNull(), isNull(), isNull())).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/library/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetByStudentId() throws Exception {
        when(libraryTransactionService.getByStudentId(studentId)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/library/transactions/student/" + studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].studentId").value(studentId.toString()));
    }

    @Test
    void testGetOverdue() throws Exception {
        LibraryTransactionResponse overdueResponse = new LibraryTransactionResponse(
                100L, 1L, "Clean Architecture", "978-0134494166",
                studentId, LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), null,
                "ISSUED", 6, new BigDecimal("30.00")
        );
        when(libraryTransactionService.getOverdueTransactions()).thenReturn(List.of(overdueResponse));

        mockMvc.perform(get("/api/library/transactions/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].overdueDays").value(6));
    }
}
