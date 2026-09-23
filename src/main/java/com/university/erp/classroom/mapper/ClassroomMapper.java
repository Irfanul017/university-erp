package com.university.erp.classroom.mapper;

import com.university.erp.classroom.dto.*;
import com.university.erp.classroom.entity.Classroom;

public final class ClassroomMapper {
    private ClassroomMapper() {
    }

    public static Classroom toEntity(ClassroomRequest request) {
        return request.classroom();
    }

    public static ClassroomResponse toResponse(Classroom entity) {
        return new ClassroomResponse(entity);
    }
}
