package com.university.erp.finance.dto;

import java.math.BigDecimal;

public record FeeRequest(
        String feeType,
        BigDecimal amount,
        String description,
        Boolean active
) {}
