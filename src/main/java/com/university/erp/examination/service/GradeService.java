package com.university.erp.examination.service;

import com.university.erp.examination.dto.GradeRequest;
import com.university.erp.examination.dto.GradeResponse;
import java.util.List;
import java.util.UUID;

public interface GradeService {
    List<GradeResponse> getAll(Long examId, UUID studentId);
    GradeResponse getById(Long gradingId);
    GradeResponse create(GradeRequest request);
    GradeResponse update(Long gradingId, GradeRequest request);
    void delete(Long gradingId);
}
