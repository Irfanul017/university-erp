package com.university.erp.finance.service;

import com.university.erp.finance.dto.LibraryTransactionRequest;
import com.university.erp.finance.dto.LibraryTransactionResponse;
import com.university.erp.finance.dto.LibraryTransactionReturnRequest;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.entity.LibraryTransaction;
import com.university.erp.finance.mapper.LibraryTransactionMapper;
import com.university.erp.finance.repository.LibraryBookRepository;
import com.university.erp.finance.repository.LibraryTransactionRepository;
import com.university.erp.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class LibraryTransactionServiceImpl implements LibraryTransactionService {

    private final LibraryTransactionRepository libraryTransactionRepository;
    private final LibraryBookRepository libraryBookRepository;
    private final StudentRepository studentRepository;
    private final BigDecimal fineRatePerDay;

    public LibraryTransactionServiceImpl(
            LibraryTransactionRepository libraryTransactionRepository,
            LibraryBookRepository libraryBookRepository,
            StudentRepository studentRepository,
            @Value("${library.fine.rate-per-day:5.00}") BigDecimal fineRatePerDay) {
        this.libraryTransactionRepository = libraryTransactionRepository;
        this.libraryBookRepository = libraryBookRepository;
        this.studentRepository = studentRepository;
        this.fineRatePerDay = fineRatePerDay != null ? fineRatePerDay : new BigDecimal("5.00");
    }

    @Override
    public LibraryTransactionResponse issueBook(LibraryTransactionRequest request) {
        if (request == null) {
            throw badRequest("Request body must not be null");
        }
        if (request.bookId() == null) {
            throw badRequest("Book ID is required");
        }
        if (request.studentId() == null) {
            throw badRequest("Student ID is required");
        }
        if (!studentRepository.existsById(request.studentId())) {
            throw notFound("Student not found with ID: " + request.studentId());
        }

        LibraryBook book = libraryBookRepository.findByIdForUpdate(request.bookId())
                .orElseThrow(() -> notFound("Book not found with ID: " + request.bookId()));

        if (book.getAvailableCopies() == null || book.getAvailableCopies() <= 0) {
            throw conflict("No available copies to issue for book ID: " + request.bookId());
        }

        LocalDate issueDate = request.issueDate() != null ? request.issueDate() : LocalDate.now();
        LocalDate dueDate = request.dueDate() != null ? request.dueDate() : issueDate.plusDays(14);

        if (dueDate.isBefore(issueDate)) {
            throw badRequest("Due date cannot be before issue date");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        libraryBookRepository.save(book);

        LibraryTransaction transaction = LibraryTransaction.builder()
                .bookId(book.getId())
                .studentId(request.studentId())
                .issueDate(issueDate)
                .dueDate(dueDate)
                .status("ISSUED")
                .build();

        LibraryTransaction saved = libraryTransactionRepository.save(transaction);
        return LibraryTransactionMapper.toResponse(saved, book, 0, BigDecimal.ZERO);
    }

    @Override
    public LibraryTransactionResponse returnBook(Long id, LibraryTransactionReturnRequest request) {
        if (id == null) {
            throw badRequest("Transaction ID must not be null");
        }
        LibraryTransaction transaction = libraryTransactionRepository.findById(id)
                .orElseThrow(() -> notFound("Library transaction not found with ID: " + id));

        if ("RETURNED".equalsIgnoreCase(transaction.getStatus())) {
            throw conflict("Book has already been returned for transaction ID: " + id);
        }

        LocalDate returnDate = (request != null && request.returnDate() != null)
                ? request.returnDate()
                : LocalDate.now();

        if (returnDate.isBefore(transaction.getIssueDate())) {
            throw badRequest("Return date cannot be before issue date");
        }

        LibraryBook book = libraryBookRepository.findByIdForUpdate(transaction.getBookId())
                .orElseThrow(() -> notFound("Book not found with ID: " + transaction.getBookId()));

        int total = book.getTotalCopies() != null ? book.getTotalCopies() : 0;
        int available = book.getAvailableCopies() != null ? book.getAvailableCopies() : 0;
        if (available < total) {
            book.setAvailableCopies(available + 1);
            libraryBookRepository.save(book);
        }

        transaction.setReturnDate(returnDate);
        transaction.setStatus("RETURNED");
        LibraryTransaction saved = libraryTransactionRepository.save(transaction);

        long overdueDays = 0;
        BigDecimal fine = BigDecimal.ZERO;
        if (transaction.getDueDate() != null && returnDate.isAfter(transaction.getDueDate())) {
            overdueDays = ChronoUnit.DAYS.between(transaction.getDueDate(), returnDate);
            fine = fineRatePerDay.multiply(BigDecimal.valueOf(overdueDays));
        }

        return LibraryTransactionMapper.toResponse(saved, book, overdueDays, fine);
    }

    @Override
    @Transactional(readOnly = true)
    public LibraryTransactionResponse get(Long id) {
        if (id == null) {
            throw badRequest("Transaction ID must not be null");
        }
        LibraryTransaction transaction = libraryTransactionRepository.findById(id)
                .orElseThrow(() -> notFound("Library transaction not found with ID: " + id));

        LibraryBook book = libraryBookRepository.findById(transaction.getBookId()).orElse(null);
        long overdueDays = calculateOverdueDays(transaction, LocalDate.now());
        BigDecimal fine = fineRatePerDay.multiply(BigDecimal.valueOf(overdueDays));

        return LibraryTransactionMapper.toResponse(transaction, book, overdueDays, fine);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryTransactionResponse> list(UUID studentId, Long bookId, String status) {
        String cleanStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;
        List<LibraryTransaction> transactions = libraryTransactionRepository.findAll().stream()
                .filter(t -> studentId == null || (t.getStudentId() != null && t.getStudentId().equals(studentId)))
                .filter(t -> bookId == null || (t.getBookId() != null && t.getBookId().equals(bookId)))
                .filter(t -> cleanStatus == null || (t.getStatus() != null && t.getStatus().equalsIgnoreCase(cleanStatus)))
                .sorted(java.util.Comparator.comparing(LibraryTransaction::getId, java.util.Comparator.nullsLast(java.util.Comparator.reverseOrder())))
                .toList();
        return mapToResponsesWithBooks(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryTransactionResponse> getByStudentId(UUID studentId) {
        if (studentId == null) {
            throw badRequest("Student ID must not be null");
        }
        if (!studentRepository.existsById(studentId)) {
            throw notFound("Student not found with ID: " + studentId);
        }
        List<LibraryTransaction> transactions = libraryTransactionRepository.findByStudentId(studentId);
        return mapToResponsesWithBooks(transactions);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryTransactionResponse> getOverdueTransactions() {
        LocalDate today = LocalDate.now();
        List<LibraryTransaction> overdueList = libraryTransactionRepository.findActiveOverdue(today);
        return mapToResponsesWithBooks(overdueList);
    }

    private List<LibraryTransactionResponse> mapToResponsesWithBooks(List<LibraryTransaction> transactions) {
        if (transactions.isEmpty()) {
            return List.of();
        }
        List<Long> bookIds = transactions.stream().map(LibraryTransaction::getBookId).distinct().toList();
        Map<Long, LibraryBook> bookMap = libraryBookRepository.findAllById(bookIds).stream()
                .collect(Collectors.toMap(LibraryBook::getId, Function.identity()));

        LocalDate today = LocalDate.now();
        return transactions.stream().map(t -> {
            LibraryBook b = bookMap.get(t.getBookId());
            long overdue = calculateOverdueDays(t, today);
            BigDecimal fine = fineRatePerDay.multiply(BigDecimal.valueOf(overdue));
            return LibraryTransactionMapper.toResponse(t, b, overdue, fine);
        }).toList();
    }

    private long calculateOverdueDays(LibraryTransaction transaction, LocalDate today) {
        if (transaction.getDueDate() == null) {
            return 0;
        }
        if ("RETURNED".equalsIgnoreCase(transaction.getStatus())) {
            if (transaction.getReturnDate() != null && transaction.getReturnDate().isAfter(transaction.getDueDate())) {
                return ChronoUnit.DAYS.between(transaction.getDueDate(), transaction.getReturnDate());
            }
            return 0;
        } else {
            if (today.isAfter(transaction.getDueDate())) {
                return ChronoUnit.DAYS.between(transaction.getDueDate(), today);
            }
            return 0;
        }
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseStatusException notFound(String message) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, message);
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}
