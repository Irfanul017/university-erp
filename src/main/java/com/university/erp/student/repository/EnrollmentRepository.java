package com.university.erp.student.repository;

import com.university.erp.student.entity.Enrollment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
	List<Enrollment> findByStudentStudentId(UUID studentId);
}
