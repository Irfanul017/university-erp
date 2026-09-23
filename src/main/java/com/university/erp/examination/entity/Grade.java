package com.university.erp.examination.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "grading")
@Getter
@Setter
@NoArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grading_id")
    private Long id;
    @Column(name = "exam_id", nullable = false)
    private Long examId;
    @Column(name = "student_id", nullable = false)
    private UUID studentId;
    @Column(name = "marks_obtained", nullable = false)
    private BigDecimal marksObtained;
    private String grade;
    @Column(name = "grade_point")
    private BigDecimal gradePoint;
    @Column(name = "result_status", nullable = false)
    private String resultStatus = "PASS";
    @Column(columnDefinition = "text")
    private String remarks;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void create() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void update() {
        updatedAt = LocalDateTime.now();
    }
}
