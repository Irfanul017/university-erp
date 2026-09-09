package com.university.erp.examination.mapper;

import com.university.erp.examination.dto.*;
import com.university.erp.examination.entity.Grade;

public final class GradeMapper {
    private GradeMapper() {
    }

    public static Grade toEntity(GradeRequest request) {
        return request.grade();
    }

    public static GradeResponse toResponse(Grade entity) {
        return new GradeResponse(entity);
    }
}
