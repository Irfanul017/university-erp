package com.university.erp.classroom.service;

import com.university.erp.classroom.entity.Classroom;
import com.university.erp.classroom.repository.ClassroomRepository;
import com.university.erp.common.JpaCrudService;
import org.springframework.stereotype.Service;

@Service
public class ClassroomServiceImpl extends JpaCrudService<Classroom, Integer> implements ClassroomService {
    public ClassroomServiceImpl(ClassroomRepository repository) {
        super(repository);
    }
}
