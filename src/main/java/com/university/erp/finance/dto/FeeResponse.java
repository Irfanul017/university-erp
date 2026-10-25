package com.university.erp.finance.dto;

import java.math.BigDecimal;

public record FeeResponse(
        Long id,
        String feeType,
        BigDecimal amount,
        String description,
        boolean active
) {}
