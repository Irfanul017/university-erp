package com.university.erp.student.mapper;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.entity.Enrollment;
import com.university.erp.student.entity.Student;
import com.university.erp.course.entity.Course;

public final class EnrollmentMapper {
	private EnrollmentMapper() { }

	public static Enrollment toEntity(EnrollmentRequest request, Student student, Course course) {
		Enrollment enrollment = new Enrollment();
		updateEntity(enrollment, request, student, course);
		return enrollment;
	}

	public static void updateEntity(Enrollment enrollment, EnrollmentRequest request, Student student, Course course) {
		enrollment.setStudent(student);
		enrollment.setCourse(course);
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
	}

	public static EnrollmentResponse toResponse(Enrollment enrollment) {
		Course course = enrollment.getCourse();
		return new EnrollmentResponse(enrollment.getEnrollmentId(), enrollment.getStudent().getStudentId(),
				enrollment.getStudentName(), course == null ? null : course.getId(),
				course == null ? null : course.getCourseCode(), course == null ? null : course.getCourseName(),
				enrollment.getSemester(), enrollment.getAcademicYear(), enrollment.getRollNo(),
				enrollment.getDivision(), enrollment.getAdmissionType(), enrollment.getEnrollmentStatus(),
				enrollment.getStartDate(), enrollment.getEndDate(), enrollment.getExitReason(),
				enrollment.getFinalCgpa());
	}
}
