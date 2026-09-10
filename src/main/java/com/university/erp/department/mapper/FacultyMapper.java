package com.university.erp.department.mapper;

import com.university.erp.department.dto.*;
import com.university.erp.department.entity.Faculty;

public final class FacultyMapper {
    private FacultyMapper() {
    }

    public static Faculty toEntity(FacultyRequest request) {
        return request.faculty();
    }

    public static FacultyResponse toResponse(Faculty entity) {
        return new FacultyResponse(entity);
    }
}
