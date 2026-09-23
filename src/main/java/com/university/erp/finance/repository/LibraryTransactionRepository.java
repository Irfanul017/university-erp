package com.university.erp.finance.repository;

import com.university.erp.finance.entity.LibraryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibraryTransactionRepository extends JpaRepository<LibraryTransaction, Long> {
}
