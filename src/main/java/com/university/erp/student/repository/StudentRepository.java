package com.university.erp.student.repository;

import com.university.erp.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
}
