package com.university.erp.finance.controller;

import com.university.erp.finance.dto.LibraryTransactionRequest;
import com.university.erp.finance.dto.LibraryTransactionResponse;
import com.university.erp.finance.dto.LibraryTransactionReturnRequest;
import com.university.erp.finance.service.LibraryTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/library/transactions")
public class LibraryTransactionController {

    private final LibraryTransactionService libraryTransactionService;

    public LibraryTransactionController(LibraryTransactionService libraryTransactionService) {
        this.libraryTransactionService = libraryTransactionService;
    }

    @PostMapping
    public ResponseEntity<LibraryTransactionResponse> issueBook(@RequestBody LibraryTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryTransactionService.issueBook(request));
    }

    @PostMapping("/{id}/return")
    public LibraryTransactionResponse returnBook(
            @PathVariable Long id,
            @RequestBody(required = false) LibraryTransactionReturnRequest request) {
        return libraryTransactionService.returnBook(id, request);
    }

    @GetMapping("/{id}")
    public LibraryTransactionResponse get(@PathVariable Long id) {
        return libraryTransactionService.get(id);
    }

    @GetMapping
    public List<LibraryTransactionResponse> list(
            @RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) Long bookId,
            @RequestParam(required = false) String status) {
        return libraryTransactionService.list(studentId, bookId, status);
    }

    @GetMapping("/student/{studentId}")
    public List<LibraryTransactionResponse> getByStudentId(@PathVariable UUID studentId) {
        return libraryTransactionService.getByStudentId(studentId);
    }

    @GetMapping("/overdue")
    public List<LibraryTransactionResponse> getOverdue() {
        return libraryTransactionService.getOverdueTransactions();
    }
}
