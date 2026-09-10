package com.university.erp.course.mapper;

import com.university.erp.course.dto.*;
import com.university.erp.course.entity.Course;

public final class CourseMapper {
    private CourseMapper() {
    }

    public static Course toEntity(CourseRequest request) {
        return request.course();
    }

    public static CourseResponse toResponse(Course entity) {
        return new CourseResponse(entity);
    }
}
