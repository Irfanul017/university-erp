package com.university.erp.examination.repository;

import com.university.erp.examination.entity.Grade;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByExamId(Long examId);
    List<Grade> findAllByStudentId(UUID studentId);
    List<Grade> findAllByExamIdAndStudentId(Long examId, UUID studentId);
    boolean existsByExamIdAndStudentId(Long examId, UUID studentId);
}
