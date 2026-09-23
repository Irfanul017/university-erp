package com.university.erp.finance.controller;

import com.university.erp.common.CrudController;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.repository.LibraryBookRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/library/books")
public class LibraryBookController extends CrudController<LibraryBook, Long> {
    public LibraryBookController(LibraryBookRepository repository) {
        super(repository);
    }
}
