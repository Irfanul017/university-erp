
package com.university.erp.classroom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.erp.classroom.entity.Classroom;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {

}

