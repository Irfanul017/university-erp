package com.university.erp.examination.mapper;

import com.university.erp.examination.dto.*;
import com.university.erp.examination.entity.Examination;

public final class ExaminationMapper {
    private ExaminationMapper() {
    }

    public static Examination toEntity(ExaminationRequest request) {
        return request.examination();
    }

    public static ExaminationResponse toResponse(Examination entity) {
        return new ExaminationResponse(entity);
    }
}
