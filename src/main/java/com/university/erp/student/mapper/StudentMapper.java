package com.university.erp.student.mapper;

import com.university.erp.student.dto.StudentRequest;
import com.university.erp.student.dto.StudentResponse;
import com.university.erp.student.entity.Student;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setRollNumber(request.getRollNumber());
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setDepartment(request.getDepartment());
        student.setProgram(request.getProgram());
        return student;
    }

    public static void updateEntity(Student student, StudentRequest request) {
        student.setRollNumber(request.getRollNumber());
        student.setName(request.getName());
        student.setEmail(request.getEmail());
        student.setPhone(request.getPhone());
        student.setAddress(request.getAddress());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setDepartment(request.getDepartment());
        student.setProgram(request.getProgram());
    }

    public static StudentResponse toResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setRollNumber(student.getRollNumber());
        response.setName(student.getName());
        response.setEmail(student.getEmail());
        response.setPhone(student.getPhone());
        response.setAddress(student.getAddress());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setDepartment(student.getDepartment());
        response.setProgram(student.getProgram());
        return response;
    }
}
