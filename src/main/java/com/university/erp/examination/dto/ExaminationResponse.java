package com.university.erp.examination.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExaminationResponse(
        Long examId,
        Long subjectId,
        String academicYear,
        Short semester,
        String examType,
        BigDecimal maxMarks,
        LocalDate examDate,
        String status,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}