package com.university.erp.examination.mapper;

import com.university.erp.examination.dto.GradeRequest;
import com.university.erp.examination.dto.GradeResponse;
import com.university.erp.examination.entity.Grade;

public final class GradeMapper {
    private GradeMapper() { }

    public static Grade toEntity(GradeRequest request) {
        Grade grade = new Grade();
        updateEntity(grade, request);
        return grade;
    }

    public static void updateEntity(Grade grade, GradeRequest request) {
        grade.setExamId(request.examId());
        grade.setStudentId(request.studentId());
        grade.setMarksObtained(request.marksObtained());
        grade.setGrade(request.grade().trim());
        grade.setGradePoint(request.gradePoint());
        grade.setResultStatus(request.resultStatus().trim());
        grade.setRemarks(request.remarks());
    }

    public static GradeResponse toResponse(Grade grade) {
        return new GradeResponse(
                grade.getGradingId(), grade.getExamId(), grade.getStudentId(), grade.getMarksObtained(),
                grade.getGrade(), grade.getGradePoint(), grade.getResultStatus(), grade.getRemarks(),
                grade.getCreatedAt(), grade.getUpdatedAt());
    }
}
