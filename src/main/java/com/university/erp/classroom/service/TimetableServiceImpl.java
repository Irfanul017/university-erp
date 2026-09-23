package com.university.erp.classroom.service;

import com.university.erp.classroom.entity.Timetable;
import com.university.erp.classroom.repository.TimetableRepository;
import com.university.erp.common.JpaCrudService;
import org.springframework.stereotype.Service;

@Service
public class TimetableServiceImpl extends JpaCrudService<Timetable, Long> implements TimetableService {
    public TimetableServiceImpl(TimetableRepository repository) {
        super(repository);
    }
}
