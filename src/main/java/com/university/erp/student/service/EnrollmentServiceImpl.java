package com.university.erp.student.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.repository.EnrollmentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class EnrollmentServiceImpl extends JpaCrudService<Enrollment, UUID> implements EnrollmentService {
    public EnrollmentServiceImpl(EnrollmentRepository repository) {
        super(repository);
    }
}
