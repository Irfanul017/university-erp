package com.university.erp.classroom.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "timetable")
@Getter
@Setter
@NoArgsConstructor
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;
    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;
    @Column(name = "division_id", nullable = false)
    private UUID divisionId;
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    @Column(nullable = false)
    private Integer semester;
    @Column(name = "academic_year", nullable = false)
    private String academicYear;
    @Column(name = "lecture_type", nullable = false)
    private String lectureType;
    @Column(nullable = false)
    private String status = "Active";
    @Column(name = "classroom_id", nullable = false)
    private Integer classroomId;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    void create() {
        createdAt = OffsetDateTime.now();
    }
}
