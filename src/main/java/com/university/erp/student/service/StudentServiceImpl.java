package com.university.erp.student.service;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import com.university.erp.student.entity.Student;
import com.university.erp.student.mapper.StudentMapper;
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
    public List<StudentResponse> findAll() {
        List<StudentResponse> students = studentRepository.findAll().stream().map(StudentMapper::toResponse).toList();
        logger.info("Listed {} students", students.size());
        return students;
    }

    @Override
    public StudentResponse findById(UUID studentId) {
        StudentResponse student = StudentMapper.toResponse(getStudent(studentId));
        logger.info("Retrieved student {}", studentId);
        return student;
    }

    @Override
    public StudentResponse create(StudentRequest request) {
        Student student = studentRepository.save(StudentMapper.toEntity(request));
        logger.info("Created student {}", student.getStudentId());
        return StudentMapper.toResponse(student);
    }

    @Override
    public StudentResponse update(UUID studentId, StudentRequest request) {
        Student student = getStudent(studentId);
        StudentMapper.updateEntity(student, request);
        Student updatedStudent = studentRepository.save(student);
        logger.info("Updated student {}", studentId);
        return StudentMapper.toResponse(updatedStudent);
    }

    @Override
    public void delete(UUID studentId) {
        Student student = getStudent(studentId);
        student.setCurrentStatus("Inactive");
        studentRepository.save(student);
        logger.info("Marked student {} as inactive", studentId);
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
