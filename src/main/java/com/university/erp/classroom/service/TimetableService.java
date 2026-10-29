
package com.university.erp.classroom.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.university.erp.classroom.entity.Timetable;
import com.university.erp.classroom.repository.TimetableRepository;

@Service
public class TimetableService {

    private final TimetableRepository timetableRepository;

    public TimetableService(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    // GET - Get all timetables
    public List<Timetable> getAllTimetables() {
        return timetableRepository.findAll();
    }

    // GET - Get timetable by ID
    public Timetable getTimetableById(Long id) {
        return timetableRepository.findById(id).orElse(null);
    }

    // POST - Create a new timetable
    public Timetable createTimetable(Timetable timetable) {
        return timetableRepository.save(timetable);
    }

    // PUT - Update timetable
    public Timetable updateTimetable(Long id, Timetable timetable) {

        Timetable existingTimetable =
                timetableRepository.findById(id).orElse(null);

        if (existingTimetable == null) {
            return null;
        }

        existingTimetable.setSubjectId(timetable.getSubjectId());
        existingTimetable.setDivisionId(timetable.getDivisionId());
        existingTimetable.setDayOfWeek(timetable.getDayOfWeek());
        existingTimetable.setStartTime(timetable.getStartTime());
        existingTimetable.setEndTime(timetable.getEndTime());
        existingTimetable.setSemester(timetable.getSemester());
        existingTimetable.setAcademicYear(timetable.getAcademicYear());
        existingTimetable.setLectureType(timetable.getLectureType());
        existingTimetable.setStatus(timetable.getStatus());
        existingTimetable.setClassroom(timetable.getClassroom());

        return timetableRepository.save(existingTimetable);
    }

    // DELETE - Delete timetable
    public void deleteTimetable(Long id) {
        timetableRepository.deleteById(id);
    }
}

