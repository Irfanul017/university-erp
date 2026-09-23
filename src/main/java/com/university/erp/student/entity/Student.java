package com.university.erp.student.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "student")
public class Student {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "student_id", nullable = false, updatable = false)
	private UUID studentId;

	@Column(name = "enrollment_no", nullable = false, unique = true)
	private String enrollmentNo;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "middle_name")
	private String middleName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	private String gender;
	private LocalDate dob;

	@Column(nullable = false, unique = true)
	private String email;

	private String phone;
	private String address;

	@Column(name = "admission_date", nullable = false)
	private LocalDate admissionDate;

	@Column(name = "current_status", nullable = false)
	private String currentStatus = "Active";

	@Column(name = "created_at", insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", insertable = false)
	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Enrollment> enrollments = new ArrayList<>();

	public UUID getStudentId() { return studentId; }
	public void setStudentId(UUID studentId) { this.studentId = studentId; }
	public String getEnrollmentNo() { return enrollmentNo; }
	public void setEnrollmentNo(String enrollmentNo) { this.enrollmentNo = enrollmentNo; }
	public String getFirstName() { return firstName; }
	public void setFirstName(String firstName) { this.firstName = firstName; }
	public String getMiddleName() { return middleName; }
	public void setMiddleName(String middleName) { this.middleName = middleName; }
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public String getGender() { return gender; }
	public void setGender(String gender) { this.gender = gender; }
	public LocalDate getDob() { return dob; }
	public void setDob(LocalDate dob) { this.dob = dob; }
	public String getEmail() { return email; }
	public void setEmail(String email) { this.email = email; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
	public String getAddress() { return address; }
	public void setAddress(String address) { this.address = address; }
	public LocalDate getAdmissionDate() { return admissionDate; }
	public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }
	public String getCurrentStatus() { return currentStatus; }
	public void setCurrentStatus(String currentStatus) { this.currentStatus = currentStatus; }
	public LocalDateTime getCreatedAt() { return createdAt; }
	public LocalDateTime getUpdatedAt() { return updatedAt; }
	public List<Enrollment> getEnrollments() { return enrollments; }
	public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
}
