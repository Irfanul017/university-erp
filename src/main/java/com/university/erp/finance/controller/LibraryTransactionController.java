package com.university.erp.finance.controller;

import com.university.erp.common.CrudController;
import com.university.erp.finance.entity.LibraryTransaction;
import com.university.erp.finance.repository.LibraryTransactionRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library/transactions")
public class LibraryTransactionController extends CrudController<LibraryTransaction, Long> {
    public LibraryTransactionController(LibraryTransactionRepository repository) {
        super(repository);
    }
}
