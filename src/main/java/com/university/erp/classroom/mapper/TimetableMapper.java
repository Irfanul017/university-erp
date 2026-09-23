package com.university.erp.classroom.mapper;

import com.university.erp.classroom.dto.*;
import com.university.erp.classroom.entity.Timetable;

public final class TimetableMapper {
    private TimetableMapper() {
    }

    public static Timetable toEntity(TimetableRequest request) {
        return request.timetable();
    }

    public static TimetableResponse toResponse(Timetable entity) {
        return new TimetableResponse(entity);
    }
}
