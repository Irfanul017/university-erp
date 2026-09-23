package com.university.erp.examination.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.*;
import java.time.*;

@Entity
@Table(name = "examination")
@Getter
@Setter
@NoArgsConstructor
public class Examination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long id;
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    @Column(name = "academic_year", nullable = false)
    private String academicYear = "2026-2027";
    @Column(nullable = false)
    private Short semester;
    @Column(name = "exam_type", nullable = false)
    private String examType;
    @Column(name = "max_marks", nullable = false)
    private BigDecimal maxMarks;
    @Column(name = "exam_date")
    private LocalDate examDate;
    @Column(nullable = false)
    private String status = "SCHEDULED";
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
