package com.university.erp.student.mapper;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;

public final class EnrollmentMapper {
	private EnrollmentMapper() { }

	public static Enrollment toEntity(EnrollmentRequest request, Student student) {
		Enrollment enrollment = new Enrollment();
		enrollment.setStudent(student);
		enrollment.setSemester(request.semester());
		enrollment.setAcademicYear(request.academicYear());
		enrollment.setRollNo(request.rollNo());
		enrollment.setDivision(request.division());
		enrollment.setAdmissionType(request.admissionType());
		if (request.enrollmentStatus() != null) enrollment.setEnrollmentStatus(request.enrollmentStatus());
		enrollment.setStartDate(request.startDate());
		enrollment.setEndDate(request.endDate());
		enrollment.setExitReason(request.exitReason());
		enrollment.setFinalCgpa(request.finalCgpa());
		return enrollment;
	}

	public static EnrollmentResponse toResponse(Enrollment enrollment) {
		return new EnrollmentResponse(enrollment.getEnrollmentId(), enrollment.getStudent().getStudentId(),
				enrollment.getSemester(), enrollment.getAcademicYear(), enrollment.getRollNo(),
				enrollment.getDivision(), enrollment.getAdmissionType(), enrollment.getEnrollmentStatus(),
				enrollment.getStartDate(), enrollment.getEndDate(), enrollment.getExitReason(),
				enrollment.getFinalCgpa());
	}
}
