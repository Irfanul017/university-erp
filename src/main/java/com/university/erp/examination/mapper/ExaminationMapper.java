package com.university.erp.examination.mapper;

import com.university.erp.examination.dto.ExaminationRequest;
import com.university.erp.examination.dto.ExaminationResponse;
import com.university.erp.examination.entity.Examination;

public final class ExaminationMapper {
    private ExaminationMapper() { }

    public static Examination toEntity(ExaminationRequest request) {
        Examination examination = new Examination();
        updateEntity(examination, request);
        return examination;
    }

    public static void updateEntity(Examination examination, ExaminationRequest request) {
        examination.setSubjectId(request.subjectId());
        examination.setAcademicYear(request.academicYear().trim());
        examination.setSemester(request.semester());
        examination.setExamType(request.examType().trim());
        examination.setMaxMarks(request.maxMarks());
        examination.setExamDate(request.examDate());
        examination.setStatus(request.status().trim());
        examination.setRemarks(request.remarks());
    }

    public static ExaminationResponse toResponse(Examination examination) {
        return new ExaminationResponse(
                examination.getExamId(), examination.getSubjectId(), examination.getAcademicYear(),
                examination.getSemester(), examination.getExamType(), examination.getMaxMarks(),
                examination.getExamDate(), examination.getStatus(), examination.getRemarks(),
                examination.getCreatedAt(), examination.getUpdatedAt());
    }
}
