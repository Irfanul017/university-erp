package com.university.erp.finance.service;

import com.university.erp.finance.dto.LibraryBookRequest;
import com.university.erp.finance.dto.LibraryBookResponse;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.mapper.LibraryBookMapper;
import com.university.erp.finance.repository.LibraryBookRepository;
import com.university.erp.finance.repository.LibraryTransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class LibraryBookServiceImpl implements LibraryBookService {

    private final LibraryBookRepository libraryBookRepository;
    private final LibraryTransactionRepository libraryTransactionRepository;

    public LibraryBookServiceImpl(LibraryBookRepository libraryBookRepository,
                                  LibraryTransactionRepository libraryTransactionRepository) {
        this.libraryBookRepository = libraryBookRepository;
        this.libraryTransactionRepository = libraryTransactionRepository;
    }

    @Override
    public LibraryBookResponse create(LibraryBookRequest request) {
        if (request == null) {
            throw badRequest("Request body must not be null");
        }
        if (request.title() == null || request.title().trim().isEmpty()) {
            throw badRequest("Book title is required");
        }
        if (request.isbn() == null || request.isbn().trim().isEmpty()) {
            throw badRequest("Book ISBN is required");
        }
        String isbn = request.isbn().trim();
        if (libraryBookRepository.existsByIsbn(isbn)) {
            throw conflict("Book with ISBN '" + isbn + "' already exists");
        }

        int totalCopies = request.totalCopies() != null ? request.totalCopies() : 0;
        int availableCopies = request.availableCopies() != null ? request.availableCopies() : totalCopies;

        if (totalCopies < 0) {
            throw badRequest("Total copies cannot be negative");
        }
        if (availableCopies < 0) {
            throw badRequest("Available copies cannot be negative");
        }
        if (availableCopies > totalCopies) {
            throw badRequest("Available copies cannot exceed total copies");
        }

        LibraryBook book = LibraryBook.builder()
                .title(request.title().trim())
                .isbn(isbn)
                .author(request.author() != null ? request.author().trim() : null)
                .publisher(request.publisher() != null ? request.publisher().trim() : null)
                .totalCopies(totalCopies)
                .availableCopies(availableCopies)
                .build();

        return LibraryBookMapper.toResponse(libraryBookRepository.save(book));
    }

    @Override
    @Transactional(readOnly = true)
    public LibraryBookResponse get(Long id) {
        return LibraryBookMapper.toResponse(findBook(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LibraryBookResponse> list(String title, String author, String isbn, Boolean availableOnly) {
        String cleanTitle = (title != null && !title.trim().isEmpty()) ? title.trim() : null;
        String cleanAuthor = (author != null && !author.trim().isEmpty()) ? author.trim() : null;
        String cleanIsbn = (isbn != null && !isbn.trim().isEmpty()) ? isbn.trim() : null;
        boolean availOnly = Boolean.TRUE.equals(availableOnly);

        return libraryBookRepository.findAll().stream()
                .filter(b -> cleanTitle == null || (b.getTitle() != null && b.getTitle().toLowerCase().contains(cleanTitle.toLowerCase())))
                .filter(b -> cleanAuthor == null || (b.getAuthor() != null && b.getAuthor().toLowerCase().contains(cleanAuthor.toLowerCase())))
                .filter(b -> cleanIsbn == null || (b.getIsbn() != null && b.getIsbn().toLowerCase().contains(cleanIsbn.toLowerCase())))
                .filter(b -> !availOnly || (b.getAvailableCopies() != null && b.getAvailableCopies() > 0))
                .sorted(java.util.Comparator.comparing(LibraryBook::getId, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder())))
                .map(LibraryBookMapper::toResponse)
                .toList();
    }

    @Override
    public LibraryBookResponse update(Long id, LibraryBookRequest request) {
        if (request == null) {
            throw badRequest("Request body must not be null");
        }
        LibraryBook book = findBook(id);

        if (request.isbn() != null && !request.isbn().trim().isEmpty()) {
            String newIsbn = request.isbn().trim();
            if (libraryBookRepository.existsByIsbnAndIdNot(newIsbn, id)) {
                throw conflict("Another book with ISBN '" + newIsbn + "' already exists");
            }
            book.setIsbn(newIsbn);
        }

        if (request.title() != null && !request.title().trim().isEmpty()) {
            book.setTitle(request.title().trim());
        }

        if (request.author() != null) {
            book.setAuthor(request.author().trim());
        }

        if (request.publisher() != null) {
            book.setPublisher(request.publisher().trim());
        }

        Integer newTotal = request.totalCopies() != null ? request.totalCopies() : book.getTotalCopies();
        Integer newAvailable = request.availableCopies() != null ? request.availableCopies() : book.getAvailableCopies();

        if (newTotal < 0) {
            throw badRequest("Total copies cannot be negative");
        }
        if (newAvailable < 0) {
            throw badRequest("Available copies cannot be negative");
        }
        if (newAvailable > newTotal) {
            throw badRequest("Available copies cannot exceed total copies");
        }

        book.setTotalCopies(newTotal);
        book.setAvailableCopies(newAvailable);

        return LibraryBookMapper.toResponse(libraryBookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        LibraryBook book = findBook(id);
        if (libraryTransactionRepository.existsByBookId(id)) {
            throw conflict("Cannot delete book referenced by existing library transactions");
        }
        libraryBookRepository.delete(book);
    }

    private LibraryBook findBook(Long id) {
        if (id == null) {
            throw badRequest("Book ID must not be null");
        }
        return libraryBookRepository.findById(id)
                .orElseThrow(() -> notFound("Library book not found with ID: " + id));
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
