package com.university.erp.finance.controller;

import com.university.erp.finance.dto.LibraryBookRequest;
import com.university.erp.finance.dto.LibraryBookResponse;
import com.university.erp.finance.service.LibraryBookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class LibraryBookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LibraryBookService libraryBookService;

    @InjectMocks
    private LibraryBookController libraryBookController;

    private LibraryBookResponse sampleResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(libraryBookController).build();
        sampleResponse = new LibraryBookResponse(
                1L, "978-0262033848", "Introduction to Algorithms",
                "Thomas H. Cormen", "MIT Press", 10, 10
        );
    }

    @Test
    void testCreateBook() throws Exception {
        when(libraryBookService.create(any(LibraryBookRequest.class))).thenReturn(sampleResponse);

        String json = """
                {
                    "isbn": "978-0262033848",
                    "title": "Introduction to Algorithms",
                    "author": "Thomas H. Cormen",
                    "publisher": "MIT Press",
                    "totalCopies": 10,
                    "availableCopies": 10
                }
                """;

        mockMvc.perform(post("/api/library/books").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Introduction to Algorithms"))
                .andExpect(jsonPath("$.totalCopies").value(10));
    }

    @Test
    void testGetBook() throws Exception {
        when(libraryBookService.get(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/library/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.isbn").value("978-0262033848"));
    }

    @Test
    void testListBooks() throws Exception {
        when(libraryBookService.list(eq("Algorithms"), isNull(), isNull(), isNull()))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/library/books?title=Algorithms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Introduction to Algorithms"));
    }

    @Test
    void testUpdateBook() throws Exception {
        LibraryBookResponse updated = new LibraryBookResponse(
                1L, "978-0262033848", "Introduction to Algorithms 4th Edition",
                "Thomas H. Cormen", "MIT Press", 12, 12
        );
        when(libraryBookService.update(eq(1L), any(LibraryBookRequest.class))).thenReturn(updated);

        String json = """
                {
                    "isbn": "978-0262033848",
                    "title": "Introduction to Algorithms 4th Edition",
                    "author": "Thomas H. Cormen",
                    "publisher": "MIT Press",
                    "totalCopies": 12,
                    "availableCopies": 12
                }
                """;

        mockMvc.perform(put("/api/library/books/1").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Introduction to Algorithms 4th Edition"))
                .andExpect(jsonPath("$.totalCopies").value(12));
    }

    @Test
    void testDeleteBook() throws Exception {
        doNothing().when(libraryBookService).delete(1L);

        mockMvc.perform(delete("/api/library/books/1"))
                .andExpect(status().isNoContent());
    }
}
