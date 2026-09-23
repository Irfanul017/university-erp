package com.university.erp.finance.controller;

import com.university.erp.common.CrudController;
import com.university.erp.finance.entity.Payment;
import com.university.erp.finance.repository.PaymentRepository;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController extends CrudController<Payment, UUID> {
    public PaymentController(PaymentRepository repository) {
        super(repository);
    }
}
