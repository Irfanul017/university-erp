package com.university.erp.examination.service;

import com.university.erp.examination.dto.ExaminationRequest;
import com.university.erp.examination.dto.ExaminationResponse;
import com.university.erp.examination.entity.Examination;
import com.university.erp.examination.mapper.ExaminationMapper;
import com.university.erp.examination.repository.ExaminationRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class ExaminationServiceImpl implements ExaminationService {
    private final ExaminationRepository examinationRepository;

    public ExaminationServiceImpl(ExaminationRepository examinationRepository) {
        this.examinationRepository = examinationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExaminationResponse> getAll(Long subjectId) {
        List<Examination> examinations = subjectId == null
                ? examinationRepository.findAll()
                : examinationRepository.findAllBySubjectId(subjectId);
        return examinations.stream().map(ExaminationMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ExaminationResponse getById(Long examId) {
        return ExaminationMapper.toResponse(findExam(examId));
    }

    @Override
    public ExaminationResponse create(ExaminationRequest request) {
        validate(request);
        return ExaminationMapper.toResponse(examinationRepository.save(ExaminationMapper.toEntity(request)));
    }

    @Override
    public ExaminationResponse update(Long examId, ExaminationRequest request) {
        validate(request);
        Examination examination = findExam(examId);
        ExaminationMapper.updateEntity(examination, request);
        return ExaminationMapper.toResponse(examinationRepository.saveAndFlush(examination));
    }

    @Override
    public void delete(Long examId) {
        examinationRepository.delete(findExam(examId));
    }

    private Examination findExam(Long examId) {
        return examinationRepository.findById(examId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Examination not found"));
    }

    private void validate(ExaminationRequest request) {
        if (request == null || request.subjectId() == null || request.subjectId() <= 0
                || isBlank(request.academicYear()) || request.semester() == null || request.semester() <= 0
                || isBlank(request.examType()) || request.maxMarks() == null
                || request.maxMarks().compareTo(BigDecimal.ZERO) <= 0 || request.examDate() == null
                || isBlank(request.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "subjectId, academicYear, semester, examType, positive maxMarks, examDate and status are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
