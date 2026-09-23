package com.university.erp.student.mapper;

import com.university.erp.student.dto.*;
import com.university.erp.student.entity.Student;

public final class StudentMapper {
    private StudentMapper() {
    }

    public static Student toEntity(StudentRequest request) {
        return request.student();
    }

    public static StudentResponse toResponse(Student entity) {
        return new StudentResponse(entity);
    }
}
