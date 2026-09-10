package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.*;
import com.university.erp.finance.entity.LibraryTransaction;

public final class LibraryTransactionMapper {
    private LibraryTransactionMapper() {
    }

    public static LibraryTransaction toEntity(LibraryTransactionRequest request) {
        return request.libraryTransaction();
    }

    public static LibraryTransactionResponse toResponse(LibraryTransaction entity) {
        return new LibraryTransactionResponse(entity);
    }
}
