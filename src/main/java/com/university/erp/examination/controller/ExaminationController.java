package com.university.erp.examination.controller;

import com.university.erp.common.CrudController;
import com.university.erp.examination.entity.Examination;
import com.university.erp.examination.repository.ExaminationRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/examinations")
public class ExaminationController extends CrudController<Examination, Long> {
    public ExaminationController(ExaminationRepository repository) {
        super(repository);
    }
}
