package com.university.erp.finance.service;

import com.university.erp.common.JpaCrudService;
import com.university.erp.finance.entity.Payment;
import com.university.erp.finance.repository.PaymentRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl extends JpaCrudService<Payment, UUID> implements PaymentService {
    public PaymentServiceImpl(PaymentRepository repository) {
        super(repository);
    }
}
