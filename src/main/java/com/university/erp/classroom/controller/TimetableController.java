package com.university.erp.classroom.controller;

import com.university.erp.common.CrudController;
import com.university.erp.classroom.entity.Timetable;
import com.university.erp.classroom.repository.TimetableRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/timetables")
public class TimetableController extends CrudController<Timetable, Long> {
    public TimetableController(TimetableRepository repository) {
        super(repository);
    }
}
