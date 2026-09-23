package com.university.erp.student.controller;

import com.university.erp.student.dto.EnrollmentRequest;
import com.university.erp.student.dto.EnrollmentResponse;
import com.university.erp.student.service.EnrollmentService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/enrollments")
@Tag(name = "Enrollments")
public class EnrollmentController {

	private final EnrollmentService enrollmentService;

	public EnrollmentController(EnrollmentService enrollmentService) {
		this.enrollmentService = enrollmentService;
	}

	@GetMapping
	@Operation(summary = "List enrollments")
	public List<EnrollmentResponse> findAll() { return enrollmentService.findAll(); }

	@GetMapping("/{enrollmentId}")
	@Operation(summary = "Get an enrollment")
	public EnrollmentResponse findById(@PathVariable UUID enrollmentId) {
		return enrollmentService.findById(enrollmentId);
	}

	@PostMapping
	@Operation(summary = "Create an enrollment")
	public ResponseEntity<EnrollmentResponse> create(@RequestBody EnrollmentRequest request) {
		EnrollmentResponse response = enrollmentService.create(request);
		return ResponseEntity.created(URI.create("/api/enrollments/" + response.enrollmentId())).body(response);
	}

	@PutMapping("/{enrollmentId}")
	@Operation(summary = "Update an enrollment")
	public EnrollmentResponse update(@PathVariable UUID enrollmentId, @RequestBody EnrollmentRequest request) {
		return enrollmentService.update(enrollmentId, request);
	}

	@DeleteMapping("/{enrollmentId}")
	@Operation(summary = "Delete an enrollment")
	public ResponseEntity<Void> delete(@PathVariable UUID enrollmentId) {
		enrollmentService.delete(enrollmentId);
		return ResponseEntity.noContent().build();
	}
}
