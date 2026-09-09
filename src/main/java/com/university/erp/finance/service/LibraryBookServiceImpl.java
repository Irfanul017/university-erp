package com.university.erp.finance.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.finance.entity.LibraryBook;
import com.university.erp.finance.repository.LibraryBookRepository;
import org.springframework.stereotype.Service;

@Service
public class LibraryBookServiceImpl extends JpaCrudService<LibraryBook, Long> implements LibraryBookService {
    public LibraryBookServiceImpl(LibraryBookRepository repository) {
        super(repository);
    }
}
