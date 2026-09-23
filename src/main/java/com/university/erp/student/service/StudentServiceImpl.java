package com.university.erp.student.service;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import com.university.erp.student.entity.Student;
import com.university.erp.student.mapper.StudentMapper;
import com.university.erp.student.repository.StudentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentResponse> findAll() {
        return studentRepository.findAll().stream().map(StudentMapper::toResponse).toList();
    }

    @Override
    public StudentResponse findById(UUID studentId) {
        return StudentMapper.toResponse(getStudent(studentId));
    }

    @Override
    public StudentResponse create(StudentRequest request) {
        return StudentMapper.toResponse(studentRepository.save(StudentMapper.toEntity(request)));
    }

    @Override
    public StudentResponse update(UUID studentId, StudentRequest request) {
        Student student = getStudent(studentId);
        StudentMapper.updateEntity(student, request);
        return StudentMapper.toResponse(studentRepository.save(student));
    }

    @Override
    public void delete(UUID studentId) {
        studentRepository.delete(getStudent(studentId));
    }

    private Student getStudent(UUID studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
    }
}