package com.university.erp.department.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.department.entity.Department;
import com.university.erp.department.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

@Service
public class DepartmentServiceImpl extends JpaCrudService<Department, Integer> implements DepartmentService {
    public DepartmentServiceImpl(DepartmentRepository repository) {
        super(repository);
    }
}
