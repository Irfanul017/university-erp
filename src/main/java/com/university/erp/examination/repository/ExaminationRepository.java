package com.university.erp.examination.repository;

import com.university.erp.examination.entity.Examination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {
}
