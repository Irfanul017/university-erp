package com.university.erp.classroom.repository;

import com.university.erp.classroom.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
}
