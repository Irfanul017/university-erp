package com.university.erp.department.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.department.entity.Faculty;
import com.university.erp.department.repository.FacultyRepository;
import org.springframework.stereotype.Service;

@Service
public class FacultyServiceImpl extends JpaCrudService<Faculty, Integer> implements FacultyService {
    public FacultyServiceImpl(FacultyRepository repository) {
        super(repository);
    }
}
