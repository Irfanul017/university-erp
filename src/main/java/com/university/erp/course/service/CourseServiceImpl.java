package com.university.erp.course.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.course.entity.Course;
import com.university.erp.course.repository.CourseRepository;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl extends JpaCrudService<Course, Long> implements CourseService {
    public CourseServiceImpl(CourseRepository repository) {
        super(repository);
    }
}
