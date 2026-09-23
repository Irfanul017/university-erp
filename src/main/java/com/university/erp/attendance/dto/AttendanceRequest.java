package com.university.erp.attendance.dto;

import com.university.erp.attendance.entity.AttendanceStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AttendanceRequest(Long lectureId, UUID studentId, AttendanceStatus status, String remarks,
        LocalDateTime markedAt, String markedBy) {
}
