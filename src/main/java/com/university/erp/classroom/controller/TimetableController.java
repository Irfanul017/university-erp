
package com.university.erp.classroom.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.erp.classroom.entity.Timetable;
import com.university.erp.classroom.service.TimetableService;

@RestController
@RequestMapping("/api/timetables")
public class TimetableController {

    private final TimetableService timetableService;

    // Constructor injection
    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    // GET - Get all timetables
    @GetMapping
    public List<Timetable> getAllTimetables() {
        return timetableService.getAllTimetables();
    }

    // GET - Get timetable by ID
    @GetMapping("/{id}")
    public Timetable getTimetableById(@PathVariable Long id) {
        return timetableService.getTimetableById(id);
    }

    // POST - Add new timetable
    @PostMapping
    public Timetable createTimetable(@RequestBody Timetable timetable) {
        return timetableService.createTimetable(timetable);
    }

    // PUT - Update timetable
    @PutMapping("/{id}")
    public Timetable updateTimetable(
            @PathVariable Long id,
            @RequestBody Timetable timetable) {

        return timetableService.updateTimetable(id, timetable);
    }

    // DELETE - Delete timetable
    @DeleteMapping("/{id}")
    public String deleteTimetable(@PathVariable Long id) {

        timetableService.deleteTimetable(id);

        return "Timetable deleted successfully";
    }
}

