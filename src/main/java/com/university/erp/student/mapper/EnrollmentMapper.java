package com.university.erp.student.mapper;

import com.university.erp.student.dto.*;
import com.university.erp.student.entity.Enrollment;

public final class EnrollmentMapper {
    private EnrollmentMapper() {
    }

    public static Enrollment toEntity(EnrollmentRequest request) {
        return request.enrollment();
    }

    public static EnrollmentResponse toResponse(Enrollment entity) {
        return new EnrollmentResponse(entity);
    }
}
