package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.LibraryTransactionResponse;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.entity.LibraryTransaction;

import java.math.BigDecimal;

public final class LibraryTransactionMapper {

    private LibraryTransactionMapper() {
    }

    public static LibraryTransactionResponse toResponse(LibraryTransaction entity, LibraryBook book, long overdueDays, BigDecimal fineAmount) {
        if (entity == null) {
            return null;
        }
        return new LibraryTransactionResponse(
                entity.getId(),
                entity.getBookId(),
                book != null ? book.getTitle() : null,
                book != null ? book.getIsbn() : null,
                entity.getStudentId(),
                entity.getIssueDate(),
                entity.getDueDate(),
                entity.getReturnDate(),
                entity.getStatus(),
                Math.max(0, overdueDays),
                fineAmount != null ? fineAmount : BigDecimal.ZERO
        );
    }
}
