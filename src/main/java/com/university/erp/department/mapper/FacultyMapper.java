package com.university.erp.department.mapper;

import org.springframework.stereotype.Component;

import com.university.erp.department.dto.FacultyResponse;
import com.university.erp.department.entity.Faculty;

@Component
public class FacultyMapper {

    public FacultyResponse toResponse(Faculty faculty) {
        return new FacultyResponse(
                faculty.getFacultyId(),
                faculty.getFirstName(),
                faculty.getLastName(),
                faculty.getEmail(),
                faculty.getPhone(),
                faculty.getDob(),
                faculty.getDesignation(),
                faculty.getQualification(),
                faculty.getSpecialization(),
                faculty.getJoiningDate(),
                faculty.getStatus(),
                faculty.getDepartment().getDepartmentId(),
                faculty.getDepartment().getDepartmentName(),
                faculty.getCreatedAt(),
                faculty.getUpdatedAt()
        );
    }
}

