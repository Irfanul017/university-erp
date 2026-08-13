package com.university.erp.student.mapper;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;

public final class EnrollmentMapper {

    private EnrollmentMapper() {
    }

    public static Enrollment toEntity(EnrollmentRequest request, Student student) {
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setAcademicYear(request.getAcademicYear());
        enrollment.setSemester(request.getSemester());
        enrollment.setCourseName(request.getCourseName());
        enrollment.setStatus(request.getStatus());
        return enrollment;
    }

    public static void updateEntity(Enrollment enrollment, EnrollmentRequest request, Student student) {
        enrollment.setStudent(student);
        enrollment.setAcademicYear(request.getAcademicYear());
        enrollment.setSemester(request.getSemester());
        enrollment.setCourseName(request.getCourseName());
        enrollment.setStatus(request.getStatus());
    }

    public static EnrollmentResponse toResponse(Enrollment enrollment) {
        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setStudentId(enrollment.getStudent().getId());
        response.setStudentName(enrollment.getStudent().getName());
        response.setAcademicYear(enrollment.getAcademicYear());
        response.setSemester(enrollment.getSemester());
        response.setCourseName(enrollment.getCourseName());
        response.setStatus(enrollment.getStatus());
        return response;
    }
}
