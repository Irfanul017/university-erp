package com.university.erp.student.controller;

import com.university.erp.common.CrudController;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.repository.EnrollmentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController extends CrudController<Enrollment, UUID> {
    public EnrollmentController(EnrollmentRepository repository) {
        super(repository);
    }
}
