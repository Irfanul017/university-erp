package com.university.erp.finance.controller;

import com.university.erp.common.CrudController;
import com.university.erp.finance.entity.Fee;
import com.university.erp.finance.repository.FeeRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fees")
public class FeeController extends CrudController<Fee, Long> {
    public FeeController(FeeRepository repository) {
        super(repository);
    }
}
