package com.university.erp.examination.controller;

import com.university.erp.examination.dto.ExaminationRequest;
import com.university.erp.examination.dto.ExaminationResponse;
import com.university.erp.examination.service.ExaminationService;
import java.util.List;
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
@RequestMapping("/api/examinations")
public class ExaminationController {
    private final ExaminationService examinationService;

    public ExaminationController(ExaminationService examinationService) {
        this.examinationService = examinationService;
    }

    @GetMapping
    public List<ExaminationResponse> getAll(@RequestParam(required = false) Long subjectId) {
        return examinationService.getAll(subjectId);
    }

    @GetMapping("/{examId}")
    public ExaminationResponse getById(@PathVariable Long examId) {
        return examinationService.getById(examId);
    }

    @PostMapping
    public ResponseEntity<ExaminationResponse> create(@RequestBody ExaminationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(examinationService.create(request));
    }

    @PutMapping("/{examId}")
    public ExaminationResponse update(@PathVariable Long examId, @RequestBody ExaminationRequest request) {
        return examinationService.update(examId, request);
    }

    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> delete(@PathVariable Long examId) {
        examinationService.delete(examId);
        return ResponseEntity.noContent().build();
    }
}