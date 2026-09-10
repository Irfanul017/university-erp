package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.*;
import com.university.erp.finance.entity.Payment;

public final class PaymentMapper {
    private PaymentMapper() {
    }

    public static Payment toEntity(PaymentRequest request) {
        return request.payment();
    }

    public static PaymentResponse toResponse(Payment entity) {
        return new PaymentResponse(entity);
    }
}
