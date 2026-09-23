package com.university.erp.attendance.dto;

import java.util.UUID;

public record AttendanceSummary(UUID studentId, long total, long present, long absent, long cWork, double percentage) {
}
