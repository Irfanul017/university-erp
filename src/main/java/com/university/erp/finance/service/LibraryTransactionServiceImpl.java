package com.university.erp.finance.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.finance.entity.LibraryTransaction;
import com.university.erp.finance.repository.LibraryTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class LibraryTransactionServiceImpl extends JpaCrudService<LibraryTransaction, Long>
        implements LibraryTransactionService {
    public LibraryTransactionServiceImpl(LibraryTransactionRepository repository) {
        super(repository);
    }
}
