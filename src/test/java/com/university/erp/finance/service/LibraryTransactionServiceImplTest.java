package com.university.erp.finance.service;

import com.university.erp.finance.dto.LibraryTransactionRequest;
import com.university.erp.finance.dto.LibraryTransactionResponse;
import com.university.erp.finance.dto.LibraryTransactionReturnRequest;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.entity.LibraryTransaction;
import com.university.erp.finance.repository.LibraryBookRepository;
import com.university.erp.finance.repository.LibraryTransactionRepository;
import com.university.erp.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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
class LibraryTransactionServiceImplTest {

    @Mock
    private LibraryTransactionRepository libraryTransactionRepository;

    @Mock
    private LibraryBookRepository libraryBookRepository;

    @Mock
    private StudentRepository studentRepository;

    private LibraryTransactionServiceImpl libraryTransactionService;

    private UUID studentId;
    private LibraryBook sampleBook;
    private LibraryTransaction sampleTransaction;

    @BeforeEach
    void setUp() {
        libraryTransactionService = new LibraryTransactionServiceImpl(
                libraryTransactionRepository,
                libraryBookRepository,
                studentRepository,
                new BigDecimal("5.00")
        );

        studentId = UUID.randomUUID();
        sampleBook = LibraryBook.builder()
                .id(1L)
                .title("Clean Architecture")
                .isbn("978-0134494166")
                .totalCopies(5)
                .availableCopies(3)
                .build();

        sampleTransaction = LibraryTransaction.builder()
                .id(100L)
                .bookId(1L)
                .studentId(studentId)
                .issueDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().minusDays(6))
                .status("ISSUED")
                .build();
    }

    @Test
    void testIssueBook_Success() {
        LibraryTransactionRequest request = new LibraryTransactionRequest(
                1L, studentId, LocalDate.now(), LocalDate.now().plusDays(14)
        );

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(libraryBookRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleBook));
        when(libraryTransactionRepository.save(any(LibraryTransaction.class))).thenAnswer(inv -> {
            LibraryTransaction t = inv.getArgument(0);
            t.setId(100L);
            return t;
        });

        LibraryTransactionResponse response = libraryTransactionService.issueBook(request);

        assertNotNull(response);
        assertEquals(100L, response.id());
        assertEquals("ISSUED", response.status());
        assertEquals(2, sampleBook.getAvailableCopies());
        verify(libraryBookRepository).save(sampleBook);
    }

    @Test
    void testIssueBook_NoCopiesAvailable_ThrowsConflict() {
        sampleBook.setAvailableCopies(0);
        LibraryTransactionRequest request = new LibraryTransactionRequest(
                1L, studentId, LocalDate.now(), LocalDate.now().plusDays(14)
        );

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(libraryBookRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleBook));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryTransactionService.issueBook(request));
        assertEquals(409, ex.getStatusCode().value());
        verify(libraryTransactionRepository, never()).save(any(LibraryTransaction.class));
    }

    @Test
    void testIssueBook_StudentNotFound_ThrowsNotFound() {
        LibraryTransactionRequest request = new LibraryTransactionRequest(
                1L, studentId, LocalDate.now(), LocalDate.now().plusDays(14)
        );

        when(studentRepository.existsById(studentId)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryTransactionService.issueBook(request));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testIssueBook_DueDateBeforeIssueDate_ThrowsBadRequest() {
        LibraryTransactionRequest request = new LibraryTransactionRequest(
                1L, studentId, LocalDate.now(), LocalDate.now().minusDays(1)
        );

        when(studentRepository.existsById(studentId)).thenReturn(true);
        when(libraryBookRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleBook));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryTransactionService.issueBook(request));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testReturnBook_Success_WithOverdueFine() {
        // Due 6 days ago, returned today -> 6 days overdue * 5.00 = 30.00
        LibraryTransactionReturnRequest request = new LibraryTransactionReturnRequest(LocalDate.now());

        when(libraryTransactionRepository.findById(100L)).thenReturn(Optional.of(sampleTransaction));
        when(libraryBookRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(sampleBook));
        when(libraryTransactionRepository.save(any(LibraryTransaction.class))).thenAnswer(inv -> inv.getArgument(0));

        LibraryTransactionResponse response = libraryTransactionService.returnBook(100L, request);

        assertNotNull(response);
        assertEquals("RETURNED", response.status());
        assertEquals(4, sampleBook.getAvailableCopies());
        assertEquals(6, response.overdueDays());
        assertEquals(new BigDecimal("30.00"), response.fineAmount());
    }

    @Test
    void testReturnBook_AlreadyReturned_ThrowsConflict() {
        sampleTransaction.setStatus("RETURNED");
        LibraryTransactionReturnRequest request = new LibraryTransactionReturnRequest(LocalDate.now());

        when(libraryTransactionRepository.findById(100L)).thenReturn(Optional.of(sampleTransaction));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryTransactionService.returnBook(100L, request));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testGetOverdueTransactions() {
        when(libraryTransactionRepository.findActiveOverdue(any(LocalDate.class)))
                .thenReturn(List.of(sampleTransaction));
        when(libraryBookRepository.findAllById(anyList())).thenReturn(List.of(sampleBook));

        List<LibraryTransactionResponse> overdue = libraryTransactionService.getOverdueTransactions();

        assertNotNull(overdue);
        assertEquals(1, overdue.size());
        assertEquals(100L, overdue.get(0).id());
        assertTrue(overdue.get(0).overdueDays() > 0);
        assertTrue(overdue.get(0).fineAmount().compareTo(BigDecimal.ZERO) > 0);
    }
}
