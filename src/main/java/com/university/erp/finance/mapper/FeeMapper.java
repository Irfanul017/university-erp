package com.university.erp.finance.mapper;

import com.university.erp.finance.dto.*;
import com.university.erp.finance.entity.Fee;

public final class FeeMapper {
    private FeeMapper() {
    }

    public static Fee toEntity(FeeRequest request) {
        return request.fee();
    }

    public static FeeResponse toResponse(Fee entity) {
        return new FeeResponse(entity);
    }
}
