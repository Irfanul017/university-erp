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

import com.university.erp.classroom.entity.Classroom;
import com.university.erp.classroom.service.ClassroomService;

@RestController
@RequestMapping("/api/classrooms")
public class ClassroomController {

    private final ClassroomService classroomService;

    // Constructor injection
    public ClassroomController(ClassroomService classroomService) {
        this.classroomService = classroomService;
    }

    // GET - Get all classrooms
    @GetMapping
    public List<Classroom> getAllClassrooms() {
        return classroomService.getAllClassrooms();
    }

    // GET - Get classroom by ID
    @GetMapping("/{id}")
    public Classroom getClassroomById(@PathVariable Integer id) {
        return classroomService.getClassroomById(id);
    }

    // POST - Add classroom
    @PostMapping
    public Classroom addClassroom(@RequestBody Classroom classroom) {
        return classroomService.addClassroom(classroom);
    }

    // PUT - Update classroom
    @PutMapping("/{id}")
    public Classroom updateClassroom(
            @PathVariable Integer id,
            @RequestBody Classroom classroom) {

        return classroomService.updateClassroom(id, classroom);
    }

    // DELETE - Delete classroom
    @DeleteMapping("/{id}")
    public String deleteClassroom(@PathVariable Integer id) {

        classroomService.deleteClassroom(id);

        return "Classroom deleted successfully";
    }
}