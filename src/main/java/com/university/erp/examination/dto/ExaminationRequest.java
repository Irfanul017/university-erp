package com.university.erp.examination.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExaminationRequest(
        Long subjectId,
        String academicYear,
        Short semester,
        String examType,
        BigDecimal maxMarks,
        LocalDate examDate,
        String status,
        String remarks) {
}