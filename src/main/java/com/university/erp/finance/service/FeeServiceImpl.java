package com.university.erp.finance.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.finance.entity.Fee;
import com.university.erp.finance.repository.FeeRepository;
import org.springframework.stereotype.Service;

@Service
public class FeeServiceImpl extends JpaCrudService<Fee, Long> implements FeeService {
    public FeeServiceImpl(FeeRepository repository) {
        super(repository);
    }
}
