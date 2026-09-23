package com.university.erp.course.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long id;
    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;
    @Column(name = "course_name", nullable = false)
    private String courseName;
    @Column(name = "course_type", nullable = false)
    private String courseType;
    @Column(name = "course_duration", nullable = false)
    private Integer courseDuration;
    @Column(name = "department_id", unique = true)
    private Integer departmentId;
}
