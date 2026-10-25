package com.university.erp.finance.service;

import com.university.erp.finance.dto.LibraryTransactionRequest;
import com.university.erp.finance.dto.LibraryTransactionResponse;
import com.university.erp.finance.dto.LibraryTransactionReturnRequest;

import java.util.List;
import java.util.UUID;

public interface LibraryTransactionService {

    LibraryTransactionResponse issueBook(LibraryTransactionRequest request);

    LibraryTransactionResponse returnBook(Long id, LibraryTransactionReturnRequest request);

    LibraryTransactionResponse get(Long id);

    List<LibraryTransactionResponse> list(UUID studentId, Long bookId, String status);

    List<LibraryTransactionResponse> getByStudentId(UUID studentId);

    List<LibraryTransactionResponse> getOverdueTransactions();
}
