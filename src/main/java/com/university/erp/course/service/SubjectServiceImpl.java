package com.university.erp.course.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.course.entity.Subject;
import com.university.erp.course.repository.SubjectRepository;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl extends JpaCrudService<Subject, Long> implements SubjectService {
    public SubjectServiceImpl(SubjectRepository repository) {
        super(repository);
    }
}
