package com.university.erp.examination.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GradeResponse(
    Long gradingId,
    Long examId,
    UUID studentId,
    BigDecimal marksObtained,
    String grade,
    BigDecimal gradePoint,
    String resultStatus,
    String remarks,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
