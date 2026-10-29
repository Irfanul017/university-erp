package com.university.erp.examination.controller;

import com.university.erp.examination.dto.GradeRequest;
import com.university.erp.examination.dto.GradeResponse;
import com.university.erp.examination.service.GradeService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grades")
public class GradeController {
    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public List<GradeResponse> getAll(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) UUID studentId) {
        return gradeService.getAll(examId, studentId);
    }

    @GetMapping("/{gradingId}")
    public GradeResponse getById(@PathVariable Long gradingId) {
        return gradeService.getById(gradingId);
    }

    @PostMapping
    public ResponseEntity<GradeResponse> create(@RequestBody GradeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gradeService.create(request));
    }

    @PutMapping("/{gradingId}")
    public GradeResponse update(@PathVariable Long gradingId, @RequestBody GradeRequest request) {
        return gradeService.update(gradingId, request);
    }

    @DeleteMapping("/{gradingId}")
    public ResponseEntity<Void> delete(@PathVariable Long gradingId) {
        gradeService.delete(gradingId);
        return ResponseEntity.noContent().build();
    }
}