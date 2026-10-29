package com.university.erp.classroom.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "classroom")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classroom_id")
    private Integer classroomId;

    @Column(name = "room_number", nullable = false, unique = true)
    private Integer roomNumber;

    @Column(name = "floor_number", nullable = false)
    private Long floorNumber;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "room_type", nullable = false)
    private String roomType;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "has_projector", nullable = false)
    private Boolean hasProjector;

    @Column(name = "has_smart_board", nullable = false)
    private Boolean hasSmartBoard;

    @Column(name = "has_ac", nullable = false)
    private Boolean hasAC;

    @OneToMany(mappedBy = "classroom")
    @JsonIgnore
    private List<Timetable> timetables;

    // Default constructor
    public Classroom() {
    }

    // Getter and Setter for classroomId
    public Integer getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(Integer classroomId) {
        this.classroomId = classroomId;
    }

    // Getter and Setter for roomNumber
    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    // Getter and Setter for floorNumber
    public Long getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Long floorNumber) {
        this.floorNumber = floorNumber;
    }

    // Getter and Setter for capacity
    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    // Getter and Setter for roomType
    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    // Getter and Setter for status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Getter and Setter for hasProjector
    public Boolean getHasProjector() {
        return hasProjector;
    }

    public void setHasProjector(Boolean hasProjector) {
        this.hasProjector = hasProjector;
    }

    // Getter and Setter for hasSmartBoard
    public Boolean getHasSmartBoard() {
        return hasSmartBoard;
    }

    public void setHasSmartBoard(Boolean hasSmartBoard) {
        this.hasSmartBoard = hasSmartBoard;
    }

    // Getter and Setter for hasAC
    public Boolean getHasAC() {
        return hasAC;
    }

    public void setHasAC(Boolean hasAC) {
        this.hasAC = hasAC;
    }

    // Getter and Setter for timetables
    public List<Timetable> getTimetables() {
        return timetables;
    }

    public void setTimetables(List<Timetable> timetables) {
        this.timetables = timetables;
    }
}