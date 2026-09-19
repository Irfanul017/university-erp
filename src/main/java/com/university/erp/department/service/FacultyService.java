package com.university.erp.department.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.university.erp.department.dto.FacultyRequest;
import com.university.erp.department.dto.FacultyResponse;
import com.university.erp.department.entity.Department;
import com.university.erp.department.entity.Faculty;
import com.university.erp.department.mapper.FacultyMapper;
import com.university.erp.department.repository.DepartmentRepository;
import com.university.erp.department.repository.FacultyRepository;

@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyMapper facultyMapper;

    public FacultyService(
            FacultyRepository facultyRepository,
            DepartmentRepository departmentRepository,
            FacultyMapper facultyMapper) {

        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.facultyMapper = facultyMapper;
    }

    public FacultyResponse create(FacultyRequest request) {

        if (facultyRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Faculty email already exists");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException(
                        "Department not found: " + request.departmentId()));

        Faculty faculty = new Faculty();

        copyRequestToEntity(request, faculty);

        faculty.setDepartment(department);

        if (faculty.getStatus() == null || faculty.getStatus().isBlank()) {
            faculty.setStatus("ACTIVE");
        }

        return facultyMapper.toResponse(facultyRepository.save(faculty));
    }

    public List<FacultyResponse> getAll() {

        return facultyRepository.findAllWithDepartment()
                .stream()
                .map(facultyMapper::toResponse)
                .toList();
    }

    public FacultyResponse getById(Integer id) {

        Faculty faculty = facultyRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new RuntimeException(
                        "Faculty not found: " + id));

        return facultyMapper.toResponse(faculty);
    }

    public FacultyResponse update(Integer id, FacultyRequest request) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Faculty not found: " + id));

        if (!faculty.getEmail().equalsIgnoreCase(request.email())
                && facultyRepository.existsByEmailIgnoreCase(request.email())) {

            throw new IllegalArgumentException(
                    "Faculty email already exists");
        }

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new RuntimeException(
                        "Department not found: " + request.departmentId()));

        copyRequestToEntity(request, faculty);

        faculty.setDepartment(department);

        facultyRepository.save(faculty);

        Faculty updatedFaculty = facultyRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new RuntimeException(
                        "Faculty not found: " + id));

        return facultyMapper.toResponse(updatedFaculty);
    }

    public void delete(Integer id) {

        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Faculty not found: " + id));

        facultyRepository.delete(faculty);
    }

    private void copyRequestToEntity(
            FacultyRequest request,
            Faculty faculty) {

        faculty.setFirstName(request.firstName());
        faculty.setLastName(request.lastName());
        faculty.setEmail(request.email());
        faculty.setPhone(request.phone());
        faculty.setDob(request.dob());
        faculty.setDesignation(request.designation());
        faculty.setQualification(request.qualification());
        faculty.setSpecialization(request.specialization());
        faculty.setJoiningDate(request.joiningDate());
        faculty.setStatus(request.status());
    }
}