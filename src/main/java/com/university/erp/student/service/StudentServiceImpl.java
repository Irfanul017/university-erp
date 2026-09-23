package com.university.erp.student.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.student.entity.Student;
import com.university.erp.student.repository.StudentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl extends JpaCrudService<Student, UUID> implements StudentService {
    public StudentServiceImpl(StudentRepository repository) {
        super(repository);
    }
}
