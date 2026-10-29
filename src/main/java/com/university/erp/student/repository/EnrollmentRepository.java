package com.university.erp.student.repository;

import com.university.erp.student.entity.Enrollment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
	@Override
	@EntityGraph(attributePaths = {"student", "course"})
	List<Enrollment> findAll();

	@Override
	@EntityGraph(attributePaths = {"student", "course"})
	Optional<Enrollment> findById(UUID enrollmentId);

	@EntityGraph(attributePaths = {"student", "course"})
	List<Enrollment> findByStudentStudentId(UUID studentId);
}
