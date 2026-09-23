package com.university.erp.student.repository;

import com.university.erp.student.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
}
