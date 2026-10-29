package com.university.erp.examination.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record GradeRequest(
        Long examId,
        UUID studentId,
        BigDecimal marksObtained,
        String grade,
        BigDecimal gradePoint,
        String resultStatus,
        String remarks) {
}