package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.*;
import com.university.erp.finance.entity.LibraryBook;

public final class LibraryBookMapper {
    private LibraryBookMapper() {
    }

    public static LibraryBook toEntity(LibraryBookRequest request) {
        return request.libraryBook();
    }

    public static LibraryBookResponse toResponse(LibraryBook entity) {
        return new LibraryBookResponse(entity);
    }
}
