package com.university.erp.student.repository;

import com.university.erp.student.entity.Student;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, UUID> {
}
