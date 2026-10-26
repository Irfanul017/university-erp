package com.university.erp.finance.service;

import com.university.erp.finance.dto.LibraryBookRequest;
import com.university.erp.finance.dto.LibraryBookResponse;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.repository.LibraryBookRepository;
import com.university.erp.finance.repository.LibraryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryBookServiceImplTest {

    @Mock
    private LibraryBookRepository libraryBookRepository;

    @Mock
    private LibraryTransactionRepository libraryTransactionRepository;

    @InjectMocks
    private LibraryBookServiceImpl libraryBookService;

    private LibraryBook sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = LibraryBook.builder()
                .id(1L)
                .title("Introduction to Algorithms")
                .isbn("978-0262033848")
                .author("Thomas H. Cormen")
                .publisher("MIT Press")
                .totalCopies(10)
                .availableCopies(10)
                .build();
    }

    @Test
    void testCreate_Success() {
        LibraryBookRequest request = new LibraryBookRequest(
                "978-0262033848", "Introduction to Algorithms", "Thomas H. Cormen", "MIT Press", 10, 10
        );

        when(libraryBookRepository.existsByIsbn("978-0262033848")).thenReturn(false);
        when(libraryBookRepository.save(any(LibraryBook.class))).thenReturn(sampleBook);

        LibraryBookResponse response = libraryBookService.create(request);

        assertNotNull(response);
        assertEquals("Introduction to Algorithms", response.title());
        assertEquals("978-0262033848", response.isbn());
        assertEquals(10, response.totalCopies());
        assertEquals(10, response.availableCopies());
    }

    @Test
    void testCreate_DuplicateIsbn_ThrowsConflict() {
        LibraryBookRequest request = new LibraryBookRequest(
                "978-0262033848", "Duplicate Book", "Author", "Publisher", 5, 5
        );
        when(libraryBookRepository.existsByIsbn("978-0262033848")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryBookService.create(request));
        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    void testCreate_AvailableExceedsTotal_ThrowsBadRequest() {
        LibraryBookRequest request = new LibraryBookRequest(
                "978-1111111111", "Book", "Author", "Publisher", 5, 10
        );
        when(libraryBookRepository.existsByIsbn("978-1111111111")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryBookService.create(request));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testCreate_NegativeCopies_ThrowsBadRequest() {
        LibraryBookRequest request = new LibraryBookRequest(
                "978-1111111111", "Book", "Author", "Publisher", -5, -5
        );
        when(libraryBookRepository.existsByIsbn("978-1111111111")).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryBookService.create(request));
        assertEquals(400, ex.getStatusCode().value());
    }

    @Test
    void testGet_Success() {
        when(libraryBookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        LibraryBookResponse response = libraryBookService.get(1L);

        assertNotNull(response);
        assertEquals(1L, response.id());
    }

    @Test
    void testGet_NotFound() {
        when(libraryBookRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryBookService.get(99L));
        assertEquals(404, ex.getStatusCode().value());
    }

    @Test
    void testList_Search() {
        when(libraryBookRepository.findAll()).thenReturn(List.of(sampleBook));

        List<LibraryBookResponse> result = libraryBookService.list("Algorithms", null, null, false);

        assertEquals(1, result.size());
        assertEquals("Introduction to Algorithms", result.get(0).title());
    }

    @Test
    void testUpdate_Success() {
        LibraryBookRequest updateRequest = new LibraryBookRequest(
                "978-0262033848", "Algorithms 4th Edition", "Cormen et al.", "MIT Press", 15, 15
        );

        when(libraryBookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(libraryBookRepository.save(any(LibraryBook.class))).thenAnswer(inv -> inv.getArgument(0));

        LibraryBookResponse response = libraryBookService.update(1L, updateRequest);

        assertNotNull(response);
        assertEquals("Algorithms 4th Edition", response.title());
        assertEquals(15, response.totalCopies());
    }

    @Test
    void testDelete_ReferencedByTransactions_ThrowsConflict() {
        when(libraryBookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(libraryTransactionRepository.existsByBookId(1L)).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> libraryBookService.delete(1L));
        assertEquals(409, ex.getStatusCode().value());
        verify(libraryBookRepository, never()).delete(any(LibraryBook.class));
    }

    @Test
    void testDelete_NotReferenced_Success() {
        when(libraryBookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(libraryTransactionRepository.existsByBookId(1L)).thenReturn(false);

        libraryBookService.delete(1L);

        verify(libraryBookRepository, times(1)).delete(sampleBook);
    }
}
