package com.university.erp.examination.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.examination.entity.Grade;
import com.university.erp.examination.repository.GradeRepository;
import org.springframework.stereotype.Service;

@Service
public class GradeServiceImpl extends JpaCrudService<Grade, Long> implements GradeService {
    public GradeServiceImpl(GradeRepository repository) {
        super(repository);
    }
}
