package com.university.erp.examination.service;

import com.university.erp.examination.dto.GradeRequest;
import com.university.erp.examination.dto.GradeResponse;
import com.university.erp.examination.entity.Examination;
import com.university.erp.examination.entity.Grade;
import com.university.erp.examination.mapper.GradeMapper;
import com.university.erp.examination.repository.ExaminationRepository;
import com.university.erp.examination.repository.GradeRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;
    private final ExaminationRepository examinationRepository;

    public GradeServiceImpl(GradeRepository gradeRepository, ExaminationRepository examinationRepository) {
        this.gradeRepository = gradeRepository;
        this.examinationRepository = examinationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeResponse> getAll(Long examId, UUID studentId) {
        List<Grade> grades;
        if (examId != null && studentId != null) {
            grades = gradeRepository.findAllByExamIdAndStudentId(examId, studentId);
        } else if (examId != null) {
            grades = gradeRepository.findAllByExamId(examId);
        } else if (studentId != null) {
            grades = gradeRepository.findAllByStudentId(studentId);
        } else {
            grades = gradeRepository.findAll();
        }
        return grades.stream().map(GradeMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GradeResponse getById(Long gradingId) {
        return GradeMapper.toResponse(findGrade(gradingId));
    }

    @Override
    public GradeResponse create(GradeRequest request) {
        validate(request);
        if (gradeRepository.existsByExamIdAndStudentId(request.examId(), request.studentId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A grade already exists for this student and examination");
        }
        return GradeMapper.toResponse(gradeRepository.save(GradeMapper.toEntity(request)));
    }

    @Override
    public GradeResponse update(Long gradingId, GradeRequest request) {
        Grade grade = findGrade(gradingId);
        validate(request);
        if ((!grade.getExamId().equals(request.examId()) || !grade.getStudentId().equals(request.studentId()))
                && gradeRepository.existsByExamIdAndStudentId(request.examId(), request.studentId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A grade already exists for this student and examination");
        }
        GradeMapper.updateEntity(grade, request);
        return GradeMapper.toResponse(gradeRepository.saveAndFlush(grade));
    }

    @Override
    public void delete(Long gradingId) {
        gradeRepository.delete(findGrade(gradingId));
    }

    private Grade findGrade(Long gradingId) {
        return gradeRepository.findById(gradingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Grade not found"));
    }

    private Examination validate(GradeRequest request) {
        if (request == null || request.examId() == null || request.examId() <= 0 || request.studentId() == null
                || request.marksObtained() == null || request.marksObtained().compareTo(BigDecimal.ZERO) < 0
                || isBlank(request.grade()) || request.gradePoint() == null
                || request.gradePoint().compareTo(BigDecimal.ZERO) < 0 || isBlank(request.resultStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "examId, studentId, non-negative marksObtained, grade, non-negative gradePoint and resultStatus are required");
        }

        Examination examination = examinationRepository.findById(request.examId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Examination not found"));
        if (request.marksObtained().compareTo(examination.getMaxMarks()) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "marksObtained cannot exceed the examination's maxMarks");
        }
        return examination;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
