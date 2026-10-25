package com.university.erp.finance.dto;

public record LibraryBookRequest(
        String isbn,
        String title,
        String author,
        String publisher,
        Integer totalCopies,
        Integer availableCopies
) {}
