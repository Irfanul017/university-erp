package com.university.erp.finance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LibraryTransactionResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String bookIsbn,
        UUID studentId,
        LocalDate issueDate,
        LocalDate dueDate,
        LocalDate returnDate,
        String status,
        long overdueDays,
        BigDecimal fineAmount
) {}
