package com.university.erp.examination.repository;

import com.university.erp.examination.entity.Examination;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {
    List<Examination> findAllBySubjectId(Long subjectId);
}
