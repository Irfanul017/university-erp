package com.university.erp.examination.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.examination.entity.Examination;
import com.university.erp.examination.repository.ExaminationRepository;
import org.springframework.stereotype.Service;

@Service
public class ExaminationServiceImpl extends JpaCrudService<Examination, Long> implements ExaminationService {
    public ExaminationServiceImpl(ExaminationRepository repository) {
        super(repository);
    }
}
