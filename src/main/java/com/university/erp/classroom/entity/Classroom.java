package com.university.erp.classroom.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;

@Entity
@Table(name = "classroom")
@Getter
@Setter
@NoArgsConstructor
public class Classroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Integer id;
    @Column(name = "timetable_id")
    private Long timetableId;
    @Column(name = "room_number", nullable = false, unique = true)
    private Integer roomNumber;
    @Column(name = "floor_number", nullable = false)
    private Long floorNumber;
    @Column(nullable = false)
    private Integer capacity;
    @Column(name = "room_type", nullable = false)
    private String roomType;
    @Column(nullable = false)
    private String status;
    @Column(name = "has_projector", nullable = false)
    private boolean hasProjector;
    @Column(name = "has_smart_board", nullable = false)
    private boolean hasSmartBoard;
    @Column(name = "has_ac", nullable = false)
    private boolean hasAc;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    void create() {
        createdAt = OffsetDateTime.now();
    }
}
