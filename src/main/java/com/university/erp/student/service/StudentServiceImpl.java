package com.university.erp.student.service;

import com.university.erp.student.entity.Student;
import com.university.erp.student.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = studentRepository.findAll();
        logger.info("Listed {} students", students.size());
        return students;
    }

    @Override
    public Student findById(UUID studentId) {
        Student student = getStudent(studentId);
        logger.info("Retrieved student {}", studentId);
        return student;
    }

    @Override
    public Student create(Student request) {
        request.setStudentId(null);
        if (request.getCurrentStatus() == null) {
            request.setCurrentStatus("Active");
        }
        Student student = studentRepository.save(request);
        logger.info("Created student {}", student.getStudentId());
        return student;
    }

    @Override
    public Student update(UUID studentId, Student request) {
        Student student = getStudent(studentId);
        student.setEnrollmentNo(request.getEnrollmentNo());
        student.setFirstName(request.getFirstName());
        student.setMiddleName(request.getMiddleName());
        student.setLastName(request.getLastName());
        student.setGender(request.getGender());
        student.setDob(request.getDob());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setAdmissionDate(request.getAdmissionDate());
        if (request.getCurrentStatus() != null) {
            student.setCurrentStatus(request.getCurrentStatus());
        }
        Student updatedStudent = studentRepository.save(student);
        logger.info("Updated student {}", studentId);
        return updatedStudent;
    }

    @Override
    public void delete(UUID studentId) {
        Student student = getStudent(studentId);
        student.setCurrentStatus("Inactive");
        studentRepository.save(student);
        logger.info("Marked student {} as inactive", studentId);
    }

    @Override
    public void hardDelete(UUID studentId) {
        Student student = getStudent(studentId);
        studentRepository.delete(student);
        logger.info("Permanently deleted student {}", studentId);
    }

    private Student getStudent(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> {
                    logger.warn("Student {} was not found", studentId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Student not found: " + studentId);
                });
    }
}
