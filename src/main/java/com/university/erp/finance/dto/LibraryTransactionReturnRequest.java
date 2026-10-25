package com.university.erp.finance.dto;

import java.time.LocalDate;

public record LibraryTransactionReturnRequest(
        LocalDate returnDate
) {}
