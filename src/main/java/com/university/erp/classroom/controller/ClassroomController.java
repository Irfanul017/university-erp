package com.university.erp.classroom.controller;

import com.university.erp.common.CrudController;
import com.university.erp.classroom.entity.Classroom;
import com.university.erp.classroom.repository.ClassroomRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController extends CrudController<Classroom, Integer> {
    public ClassroomController(ClassroomRepository repository) {
        super(repository);
    }
}
