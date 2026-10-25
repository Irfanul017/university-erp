package com.university.erp.finance.dto;

public record LibraryBookResponse(
        Long id,
        String isbn,
        String title,
        String author,
        String publisher,
        Integer totalCopies,
        Integer availableCopies
) {}
