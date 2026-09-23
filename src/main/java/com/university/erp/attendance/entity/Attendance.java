package com.university.erp.attendance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "attendance", uniqueConstraints = @UniqueConstraint(name = "uk_attendance_lecture_student", columnNames = {
        "lecture_id", "student_id" }))
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;
    @Column(name = "lecture_id", nullable = false)
    private Long lectureId;
    @Column(name = "student_id", nullable = false)
    private UUID studentId;
    @Convert(converter = AttendanceStatusConverter.class)
    @Column(nullable = false)
    private AttendanceStatus status;
    @Column(columnDefinition = "text")
    private String remarks;
    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (markedAt == null)
            markedAt = now;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getLectureId() {
        return lectureId;
    }

    public UUID getStudentId() {
        return studentId;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    public LocalDateTime getMarkedAt() {
        return markedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setLectureId(Long v) {
        lectureId = v;
    }

    public void setStudentId(UUID v) {
        studentId = v;
    }

    public void setStatus(AttendanceStatus v) {
        status = v;
    }

    public void setRemarks(String v) {
        remarks = v;
    }

    public void setMarkedAt(LocalDateTime v) {
        markedAt = v;
    }

    public void setCreatedBy(String v) {
        createdBy = v;
    }

    public void setUpdatedBy(String v) {
        updatedBy = v;
    }
}
