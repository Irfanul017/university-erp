package com.university.erp.attendance.repository;

import com.university.erp.attendance.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    boolean existsByLectureIdAndStudentId(Long lectureId, UUID studentId);

    Optional<Attendance> findByLectureIdAndStudentId(Long lectureId, UUID studentId);

    List<Attendance> findByLectureId(Long lectureId);

    List<Attendance> findByStudentId(UUID studentId);

    List<Attendance> findByLectureIdAndStatus(Long lectureId, AttendanceStatus status);

    List<Attendance> findByStudentIdAndLectureId(UUID studentId, Long lectureId);
}
