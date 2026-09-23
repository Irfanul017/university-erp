package com.university.erp.attendance.service;

import com.university.erp.attendance.dto.*;
import com.university.erp.attendance.entity.AttendanceStatus;
import java.util.*;

public interface AttendanceService {
    AttendanceResponse create(AttendanceRequest request);

    AttendanceResponse get(Long id);

    List<AttendanceResponse> list(Long lectureId, UUID studentId, AttendanceStatus status);

    AttendanceResponse update(Long id, AttendanceRequest request);

    void delete(Long id);

    List<AttendanceResponse> markBulk(List<AttendanceRequest> requests);

    AttendanceSummary summary(UUID studentId, Long lectureId);
}
