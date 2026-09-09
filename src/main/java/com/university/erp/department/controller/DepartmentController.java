package com.university.erp.department.controller;

import com.university.erp.common.CrudController;
import com.university.erp.department.entity.Department;
import com.university.erp.department.repository.DepartmentRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController extends CrudController<Department, Integer> {
    public DepartmentController(DepartmentRepository repository) {
        super(repository);
    }
}
