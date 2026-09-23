package com.university.erp.course.mapper;

import com.university.erp.course.dto.*;
import com.university.erp.course.entity.Subject;

public final class SubjectMapper {
    private SubjectMapper() {
    }

    public static Subject toEntity(SubjectRequest request) {
        return request.subject();
    }

    public static SubjectResponse toResponse(Subject entity) {
        return new SubjectResponse(entity);
    }
}
