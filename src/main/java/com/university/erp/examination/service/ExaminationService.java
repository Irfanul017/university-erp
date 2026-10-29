package com.university.erp.examination.service;

import com.university.erp.examination.dto.ExaminationRequest;
import com.university.erp.examination.dto.ExaminationResponse;
import java.util.List;

public interface ExaminationService {
    List<ExaminationResponse> getAll(Long subjectId);
    ExaminationResponse getById(Long examId);
    ExaminationResponse create(ExaminationRequest request);
    ExaminationResponse update(Long examId, ExaminationRequest request);
    void delete(Long examId);
}
