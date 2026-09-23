package com.university.erp.examination.controller;

import com.university.erp.common.CrudController;
import com.university.erp.examination.entity.Grade;
import com.university.erp.examination.repository.GradeRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grades")
public class GradeController extends CrudController<Grade, Long> {
    public GradeController(GradeRepository repository) {
        super(repository);
    }
}
