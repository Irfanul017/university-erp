package com.university.erp.attendance.controller;

import com.university.erp.attendance.dto.*;
import com.university.erp.attendance.entity.AttendanceStatus;
import com.university.erp.attendance.service.AttendanceService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AttendanceResponse> create(@RequestBody AttendanceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public AttendanceResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<AttendanceResponse> list(@RequestParam(required = false) Long lectureId,
            @RequestParam(required = false) UUID studentId, @RequestParam(required = false) AttendanceStatus status) {
        return service.list(lectureId, studentId, status);
    }

    @PutMapping("/{id}")
    public AttendanceResponse update(@PathVariable Long id, @RequestBody AttendanceRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/bulk")
    public List<AttendanceResponse> markBulk(@RequestBody List<AttendanceRequest> requests) {
        return service.markBulk(requests);
    }

    @GetMapping("/summary/{studentId}")
    public AttendanceSummary summary(@PathVariable UUID studentId, @RequestParam(required = false) Long lectureId) {
        return service.summary(studentId, lectureId);
    }
}
