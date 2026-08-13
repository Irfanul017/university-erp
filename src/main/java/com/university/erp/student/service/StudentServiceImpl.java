package com.university.erp.student.service;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import com.university.erp.student.entity.Student;
import com.university.erp.student.mapper.StudentMapper;
import com.university.erp.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Override
    public StudentResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        return StudentMapper.toResponse(student);
    }

    @Override
    public List<StudentResponse> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartmentIgnoreCase(department).stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Override
    public List<StudentResponse> getStudentsByProgram(String program) {
        return studentRepository.findByProgramIgnoreCase(program).stream()
                .map(StudentMapper::toResponse)
                .toList();
    }

    @Override
    public StudentResponse createStudent(StudentRequest request) {
        if (studentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Student with email already exists");
        }
        if (studentRepository.findByRollNumber(request.getRollNumber()).isPresent()) {
            throw new IllegalArgumentException("Student with roll number already exists");
        }
        Student student = StudentMapper.toEntity(request);
        return StudentMapper.toResponse(studentRepository.save(student));
    }

    @Override
    public StudentResponse updateStudent(Long id, StudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        StudentMapper.updateEntity(student, request);
        return StudentMapper.toResponse(studentRepository.save(student));
    }

    @Override
    public void deleteStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        studentRepository.delete(student);
    }
}
