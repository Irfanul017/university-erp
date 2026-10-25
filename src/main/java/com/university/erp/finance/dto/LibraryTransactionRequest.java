package com.university.erp.finance.dto;

import java.time.LocalDate;
import java.util.UUID;

public record LibraryTransactionRequest(
        Long bookId,
        UUID studentId,
        LocalDate issueDate,
        LocalDate dueDate
) {}
