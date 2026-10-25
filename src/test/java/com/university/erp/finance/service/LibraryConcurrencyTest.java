package com.university.erp.finance.service;

import com.university.erp.finance.dto.LibraryTransactionRequest;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.repository.LibraryBookRepository;
import com.university.erp.finance.repository.LibraryTransactionRepository;
import com.university.erp.student.entity.Student;
import com.university.erp.student.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LibraryConcurrencyTest {

    @Autowired
    private LibraryTransactionService libraryTransactionService;

    @Autowired
    private LibraryBookRepository libraryBookRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LibraryTransactionRepository libraryTransactionRepository;

    @Test
    void testConcurrentIssue_OnlyOneSucceedsWhenSingleCopyAvailable() throws Exception {
        // Create student
        Student student = new Student();
        student.setEnrollmentNo("CONC-" + UUID.randomUUID());
        student.setFirstName("John");
        student.setLastName("Doe");
        student.setEmail("john.doe." + UUID.randomUUID() + "@example.com");
        student.setAdmissionDate(LocalDate.now());
        student.setCurrentStatus("Active");
        Student savedStudent = studentRepository.save(student);

        // Create book with only 1 copy available
        LibraryBook book = LibraryBook.builder()
                .title("Concurrency In Practice")
                .isbn("ISBN-" + UUID.randomUUID())
                .totalCopies(1)
                .availableCopies(1)
                .build();
        LibraryBook savedBook = libraryBookRepository.save(book);

        int numberOfThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            futures.add(executor.submit(() -> {
                try {
                    latch.await();
                    libraryTransactionService.issueBook(new LibraryTransactionRequest(
                            savedBook.getId(),
                            savedStudent.getStudentId(),
                            LocalDate.now(),
                            LocalDate.now().plusDays(14)
                    ));
                    successCount.incrementAndGet();
                } catch (ResponseStatusException ex) {
                    if (ex.getStatusCode().value() == 409) {
                        conflictCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                }
            }));
        }

        latch.countDown();
        for (Future<?> f : futures) {
            f.get(10, TimeUnit.SECONDS);
        }
        executor.shutdown();

        assertEquals(1, successCount.get(), "Exactly one issue attempt must succeed when only 1 copy is available");
        assertEquals(numberOfThreads - 1, conflictCount.get(), "All other concurrent attempts must receive conflict (409)");

        LibraryBook updatedBook = libraryBookRepository.findById(savedBook.getId()).orElseThrow();
        assertEquals(0, updatedBook.getAvailableCopies(), "Available copies should be 0");
    }
}
