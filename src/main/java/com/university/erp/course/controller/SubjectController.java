package com.university.erp.course.controller;

import com.university.erp.common.CrudController;
import com.university.erp.course.entity.Subject;
import com.university.erp.course.repository.SubjectRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController extends CrudController<Subject, Long> {
    public SubjectController(SubjectRepository repository) {
        super(repository);
    }
}
