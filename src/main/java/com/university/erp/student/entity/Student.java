package com.university.erp.student.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

@Entity
@Table(name = "student")
@Getter
@Setter
@NoArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "student_id")
    private UUID id;
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
    @Column(columnDefinition = "text")
    private String address;
    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;
    @Column(name = "current_status", nullable = false)
    private String currentStatus = "Active";
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
