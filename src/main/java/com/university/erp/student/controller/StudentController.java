package com.university.erp.student.controller;

import com.university.erp.common.CrudController;
import com.university.erp.student.entity.Student;
import com.university.erp.student.repository.StudentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentController extends CrudController<Student, UUID> {
    public StudentController(StudentRepository repository) {
        super(repository);
    }
}
