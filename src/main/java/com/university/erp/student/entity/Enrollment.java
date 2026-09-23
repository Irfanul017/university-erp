package com.university.erp.student.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "enrollment")
public class Enrollment {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "enrollment_id", nullable = false, updatable = false)
	private UUID enrollmentId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false)
	private Student student;

	private Integer semester;

	@Column(name = "academic_year", nullable = false)
	private String academicYear;

	@Column(name = "roll_no", nullable = false)
	private Integer rollNo;

	@Column(nullable = false)
	private String division;

	@Column(name = "admission_type")
	private String admissionType;

	@Column(name = "enrollment_status")
	private String enrollmentStatus = "Active";

	@Column(name = "start_date", nullable = false)
	private LocalDate startDate;

	@Column(name = "end_date")
	private LocalDate endDate;

	@Column(name = "exit_reason")
	private String exitReason;

	private BigDecimal finalCgpa;

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public UUID getEnrollmentId() { return enrollmentId; }
	public void setEnrollmentId(UUID enrollmentId) { this.enrollmentId = enrollmentId; }
	public Student getStudent() { return student; }
	public void setStudent(Student student) { this.student = student; }
	public Integer getSemester() { return semester; }
	public void setSemester(Integer semester) { this.semester = semester; }
	public String getAcademicYear() { return academicYear; }
	public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
	public Integer getRollNo() { return rollNo; }
	public void setRollNo(Integer rollNo) { this.rollNo = rollNo; }
	public String getDivision() { return division; }
	public void setDivision(String division) { this.division = division; }
	public String getAdmissionType() { return admissionType; }
	public void setAdmissionType(String admissionType) { this.admissionType = admissionType; }
	public String getEnrollmentStatus() { return enrollmentStatus; }
	public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }
	public LocalDate getStartDate() { return startDate; }
	public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
	public LocalDate getEndDate() { return endDate; }
	public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
	public String getExitReason() { return exitReason; }
	public void setExitReason(String exitReason) { this.exitReason = exitReason; }
	public BigDecimal getFinalCgpa() { return finalCgpa; }
	public void setFinalCgpa(BigDecimal finalCgpa) { this.finalCgpa = finalCgpa; }
	public LocalDateTime getCreatedAt() { return createdAt; }
}
