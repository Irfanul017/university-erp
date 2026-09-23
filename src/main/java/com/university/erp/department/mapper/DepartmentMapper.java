package com.university.erp.department.mapper;

import com.university.erp.department.dto.DepartmentRequest;
import com.university.erp.department.dto.DepartmentResponse;
import com.university.erp.department.entity.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequest request) {
        Department department = new Department();
        department.setDepartmentName(request.departmentName());
        department.setLocation(request.location());
        return department;
    }

    public DepartmentResponse toResponse(Department department) {
        Integer hodId = department.getHod() != null
                ? department.getHod().getFacultyId()
                : null;

        return new DepartmentResponse(
                department.getDepartmentId(),
                department.getDepartmentName(),
                department.getLocation(),
                hodId,
                department.getCreatedAt(),
                department.getUpdatedAt()
        );
    }
}
