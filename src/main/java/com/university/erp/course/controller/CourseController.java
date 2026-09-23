package com.university.erp.course.controller;

import com.university.erp.common.CrudController;
import com.university.erp.course.entity.Course;
import com.university.erp.course.repository.CourseRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController extends CrudController<Course, Long> {
    public CourseController(CourseRepository repository) {
        super(repository);
    }
}
