package com.university.erp.department.mapper;

import com.university.erp.department.dto.*;
import com.university.erp.department.entity.Department;

public final class DepartmentMapper {
    private DepartmentMapper() {
    }

    public static Department toEntity(DepartmentRequest request) {
        return request.department();
    }

    public static DepartmentResponse toResponse(Department entity) {
        return new DepartmentResponse(entity);
    }
}
