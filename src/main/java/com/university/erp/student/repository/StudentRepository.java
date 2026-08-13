package com.university.erp.student.repository;

import com.university.erp.student.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByRollNumber(String rollNumber);

    List<Student> findByDepartmentIgnoreCase(String department);

    List<Student> findByProgramIgnoreCase(String program);
}
