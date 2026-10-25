package com.university.erp.finance.controller;

import com.university.erp.finance.dto.LibraryBookRequest;
import com.university.erp.finance.dto.LibraryBookResponse;
import com.university.erp.finance.service.LibraryBookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library/books")
public class LibraryBookController {

    private final LibraryBookService libraryBookService;

    public LibraryBookController(LibraryBookService libraryBookService) {
        this.libraryBookService = libraryBookService;
    }

    @PostMapping
    public ResponseEntity<LibraryBookResponse> create(@RequestBody LibraryBookRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryBookService.create(request));
    }

    @GetMapping("/{id}")
    public LibraryBookResponse get(@PathVariable Long id) {
        return libraryBookService.get(id);
    }

    @GetMapping
    public List<LibraryBookResponse> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Boolean availableOnly) {
        return libraryBookService.list(title, author, isbn, availableOnly);
    }

    @PutMapping("/{id}")
    public LibraryBookResponse update(@PathVariable Long id, @RequestBody LibraryBookRequest request) {
        return libraryBookService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        libraryBookService.delete(id);
    }
}
