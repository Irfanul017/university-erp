package com.university.erp.attendance.service;

import com.university.erp.attendance.dto.*;
import com.university.erp.attendance.entity.*;
import com.university.erp.attendance.mapper.AttendanceMapper;
import com.university.erp.attendance.repository.AttendanceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
@Transactional
public class AttendanceServiceImpl implements AttendanceService {
    private final AttendanceRepository repository;

    public AttendanceServiceImpl(AttendanceRepository repository) {
        this.repository = repository;
    }

    public AttendanceResponse create(AttendanceRequest r) {
        validate(r);
        if (repository.existsByLectureIdAndStudentId(r.lectureId(), r.studentId()))
            throw conflict("Attendance is already marked for this student and lecture");
        Attendance a = new Attendance();
        AttendanceMapper.apply(r, a);
        a.setCreatedBy(r.markedBy());
        return AttendanceMapper.toResponse(repository.save(a));
    }

    @Transactional(readOnly = true)
    public AttendanceResponse get(Long id) {
        return AttendanceMapper.toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> list(Long lectureId, UUID studentId, AttendanceStatus status) {
        List<Attendance> records;
        if (lectureId != null && studentId != null)
            records = repository.findByStudentIdAndLectureId(studentId, lectureId);
        else if (lectureId != null)
            records = status == null ? repository.findByLectureId(lectureId)
                    : repository.findByLectureIdAndStatus(lectureId, status);
        else if (studentId != null)
            records = repository.findByStudentId(studentId);
        else
            records = repository.findAll();
        return records.stream().filter(a -> status == null || a.getStatus() == status).map(AttendanceMapper::toResponse)
                .toList();
    }

    public AttendanceResponse update(Long id, AttendanceRequest r) {
        validate(r);
        Attendance a = find(id);
        if ((!a.getLectureId().equals(r.lectureId()) || !a.getStudentId().equals(r.studentId()))
                && repository.existsByLectureIdAndStudentId(r.lectureId(), r.studentId()))
            throw conflict("Attendance is already marked for this student and lecture");
        AttendanceMapper.apply(r, a);
        return AttendanceMapper.toResponse(repository.save(a));
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    public List<AttendanceResponse> markBulk(List<AttendanceRequest> requests) {
        if (requests == null || requests.isEmpty())
            throw badRequest("At least one attendance record is required");
        Set<String> keys = new HashSet<>();
        for (AttendanceRequest r : requests) {
            validate(r);
            if (!keys.add(r.lectureId() + ":" + r.studentId()))
                throw badRequest("Duplicate student and lecture in batch");
        }
        return requests.stream()
                .map(r -> repository.existsByLectureIdAndStudentId(r.lectureId(), r.studentId()) ? updateExisting(r)
                        : create(r))
                .toList();
    }

    @Transactional(readOnly = true)
    public AttendanceSummary summary(UUID studentId, Long lectureId) {
        List<Attendance> rs = lectureId == null ? repository.findByStudentId(studentId)
                : repository.findByStudentIdAndLectureId(studentId, lectureId);
        long present = rs.stream().filter(a -> a.getStatus() == AttendanceStatus.Present).count(),
                absent = rs.stream().filter(a -> a.getStatus() == AttendanceStatus.Absent).count(),
                cwork = rs.stream().filter(a -> a.getStatus() == AttendanceStatus.C_WORK).count(), total = rs.size();
        return new AttendanceSummary(studentId, total, present, absent, cwork,
                total == 0 ? 0 : Math.round((present + cwork) * 10000.0 / total) / 100.0);
    }

    private AttendanceResponse updateExisting(AttendanceRequest r) {
        Attendance a = repository.findByLectureIdAndStudentId(r.lectureId(), r.studentId()).orElseThrow();
        AttendanceMapper.apply(r, a);
        return AttendanceMapper.toResponse(repository.save(a));
    }

    private Attendance find(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attendance record not found: " + id));
    }

    private void validate(AttendanceRequest r) {
        if (r == null || r.lectureId() == null || r.lectureId() < 1 || r.studentId() == null || r.status() == null)
            throw badRequest("lectureId, studentId and status are required");
    }

    private ResponseStatusException badRequest(String m) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, m);
    }

    private ResponseStatusException conflict(String m) {
        return new ResponseStatusException(HttpStatus.CONFLICT, m);
    }
}
