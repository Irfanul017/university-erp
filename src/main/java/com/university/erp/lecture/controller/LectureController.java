package com.university.erp.lecture.controller;

import com.university.erp.common.CrudController;
import com.university.erp.lecture.entity.Lecture;
import com.university.erp.lecture.repository.LectureRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/lectures")
public class LectureController extends CrudController<Lecture, Long> {
    public LectureController(LectureRepository repository) {
        super(repository);
    }
}
