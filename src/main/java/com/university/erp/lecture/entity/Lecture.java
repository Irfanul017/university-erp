package com.university.erp.lecture.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "lecture")
@Getter
@Setter
@NoArgsConstructor
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;
    @Column(name = "subject_id", nullable = false)
    private Long subjectId;
    @Column(name = "faculty_id", nullable = false)
    private Integer facultyId;
    @Column(name = "classroom_id")
    private Long classroomId;
    @Column(name = "lecture_date", nullable = false)
    private LocalDate lectureDate;
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    @Column(name = "lecture_type")
    private String lectureType;
    private String topic;
    private String status = "Scheduled";
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

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
