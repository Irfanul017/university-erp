package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.LibraryBookRequest;
import com.university.erp.finance.dto.LibraryBookResponse;
import com.university.erp.finance.entity.LibraryBook;

public final class LibraryBookMapper {

    private LibraryBookMapper() {
    }

    public static LibraryBook toEntity(LibraryBookRequest request) {
        if (request == null) {
            return null;
        }
        int total = request.totalCopies() != null ? request.totalCopies() : 0;
        int available = request.availableCopies() != null ? request.availableCopies() : total;
        return LibraryBook.builder()
                .isbn(request.isbn() != null ? request.isbn().trim() : null)
                .title(request.title() != null ? request.title().trim() : null)
                .author(request.author() != null ? request.author().trim() : null)
                .publisher(request.publisher() != null ? request.publisher().trim() : null)
                .totalCopies(total)
                .availableCopies(available)
                .build();
    }

    public static LibraryBookResponse toResponse(LibraryBook entity) {
        if (entity == null) {
            return null;
        }
        return new LibraryBookResponse(
                entity.getId(),
                entity.getIsbn(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getPublisher(),
                entity.getTotalCopies(),
                entity.getAvailableCopies()
        );
    }

    public static void updateEntity(LibraryBook entity, LibraryBookRequest request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.isbn() != null && !request.isbn().isBlank()) {
            entity.setIsbn(request.isbn().trim());
        }
        if (request.title() != null && !request.title().isBlank()) {
            entity.setTitle(request.title().trim());
        }
        if (request.author() != null) {
            entity.setAuthor(request.author().trim());
        }
        if (request.publisher() != null) {
            entity.setPublisher(request.publisher().trim());
        }
        if (request.totalCopies() != null) {
            entity.setTotalCopies(request.totalCopies());
        }
        if (request.availableCopies() != null) {
            entity.setAvailableCopies(request.availableCopies());
        }
    }
}
