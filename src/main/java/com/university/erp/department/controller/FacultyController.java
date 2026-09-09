package com.university.erp.department.controller;

import com.university.erp.common.CrudController;
import com.university.erp.department.entity.Faculty;
import com.university.erp.department.repository.FacultyRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/faculties")
public class FacultyController extends CrudController<Faculty, Integer> {
    public FacultyController(FacultyRepository repository) {
        super(repository);
    }
}
