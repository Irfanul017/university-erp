package com.university.erp.attendance.mapper;

import com.university.erp.attendance.dto.*;
import com.university.erp.attendance.entity.Attendance;

public final class AttendanceMapper {
    private AttendanceMapper() {
    }

    public static AttendanceResponse toResponse(Attendance a) {
        return new AttendanceResponse(a.getId(), a.getLectureId(), a.getStudentId(), a.getStatus(), a.getRemarks(),
                a.getMarkedAt(), a.getCreatedAt(), a.getUpdatedAt(), a.getCreatedBy(), a.getUpdatedBy());
    }

    public static void apply(AttendanceRequest r, Attendance a) {
        a.setLectureId(r.lectureId());
        a.setStudentId(r.studentId());
        a.setStatus(r.status());
        a.setRemarks(r.remarks());
        a.setMarkedAt(r.markedAt());
        a.setUpdatedBy(r.markedBy());
    }
}
