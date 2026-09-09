package com.university.erp.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "enrollment")
@Getter
@Setter
@NoArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "enrollment_id")
    private UUID id;
    @Column(name = "student_id", nullable = false)
    private UUID studentId;
    @Column(nullable = false)
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
    @Column(name = "exit_reason", columnDefinition = "text")
    private String exitReason;
    @Column(name = "final_cgpa")
    private java.math.BigDecimal finalCgpa;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    void create() {
        createdAt = LocalDateTime.now();
    }
}
