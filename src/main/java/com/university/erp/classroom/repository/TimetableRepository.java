
package com.university.erp.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.university.erp.classroom.entity.Timetable;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {

}

