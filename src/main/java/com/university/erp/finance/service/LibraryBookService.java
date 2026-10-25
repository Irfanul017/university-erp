package com.university.erp.finance.service;

import com.university.erp.finance.dto.LibraryBookRequest;
import com.university.erp.finance.dto.LibraryBookResponse;

import java.util.List;

public interface LibraryBookService {

    LibraryBookResponse create(LibraryBookRequest request);

    LibraryBookResponse get(Long id);

    List<LibraryBookResponse> list(String title, String author, String isbn, Boolean availableOnly);

    LibraryBookResponse update(Long id, LibraryBookRequest request);

    void delete(Long id);
}
