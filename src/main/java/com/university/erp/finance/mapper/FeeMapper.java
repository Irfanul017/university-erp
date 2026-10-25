package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.FeeRequest;
import com.university.erp.finance.dto.FeeResponse;
import com.university.erp.finance.entity.Fee;

public final class FeeMapper {

    private FeeMapper() {
    }

    public static Fee toEntity(FeeRequest request) {
        if (request == null) {
            return null;
        }
        return Fee.builder()
                .feeType(request.feeType() != null ? request.feeType().trim() : null)
                .amount(request.amount())
                .description(request.description() != null ? request.description().trim() : null)
                .active(request.active() == null || request.active())
                .build();
    }

    public static FeeResponse toResponse(Fee entity) {
        if (entity == null) {
            return null;
        }
        return new FeeResponse(
                entity.getId(),
                entity.getFeeType(),
                entity.getAmount(),
                entity.getDescription(),
                entity.isActive()
        );
    }

    public static void updateEntity(Fee entity, FeeRequest request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.feeType() != null && !request.feeType().isBlank()) {
            entity.setFeeType(request.feeType().trim());
        }
        if (request.amount() != null) {
            entity.setAmount(request.amount());
        }
        if (request.description() != null) {
            entity.setDescription(request.description().trim());
        }
        if (request.active() != null) {
            entity.setActive(request.active());
        }
    }
}
