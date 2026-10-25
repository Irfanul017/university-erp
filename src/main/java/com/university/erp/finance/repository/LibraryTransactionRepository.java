package com.university.erp.finance.repository;

import com.university.erp.finance.entity.LibraryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LibraryTransactionRepository extends JpaRepository<LibraryTransaction, Long> {

    List<LibraryTransaction> findByStudentId(UUID studentId);

    List<LibraryTransaction> findByBookId(Long bookId);

    boolean existsByBookId(Long bookId);

    List<LibraryTransaction> findByStatusIgnoreCase(String status);

    @Query("SELECT t FROM LibraryTransaction t WHERE t.status = 'ISSUED' AND t.dueDate < :currentDate ORDER BY t.dueDate ASC")
    List<LibraryTransaction> findActiveOverdue(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT t FROM LibraryTransaction t WHERE " +
            "(:studentId IS NULL OR t.studentId = :studentId) AND " +
            "(:bookId IS NULL OR t.bookId = :bookId) AND " +
            "(:status IS NULL OR LOWER(t.status) = LOWER(:status)) " +
            "ORDER BY t.id DESC")
    List<LibraryTransaction> searchTransactions(
            @Param("studentId") UUID studentId,
            @Param("bookId") Long bookId,
            @Param("status") String status);
}
