
package com.university.erp.classroom.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.university.erp.classroom.entity.Classroom;
import com.university.erp.classroom.repository.ClassroomRepository;

@Service
public class ClassroomService {

    private final ClassroomRepository classroomRepository;

    private static final Logger logger =
            LoggerFactory.getLogger(ClassroomService.class);

    // Constructor injection
    public ClassroomService(ClassroomRepository classroomRepository) {
        this.classroomRepository = classroomRepository;
    }

    // GET - Get all classrooms
    public List<Classroom> getAllClassrooms() {
        logger.info("Fetching all classrooms");
        return classroomRepository.findAll();
    }

    // GET - Get classroom by ID
    public Classroom getClassroomById(Integer id) {
        logger.info("Fetching classroom with ID: {}", id);

        Classroom classroom = classroomRepository.findById(id).orElse(null);

        if (classroom == null) {
            logger.warn("Classroom not found with ID: {}", id);
        }

        return classroom;
    }

    // POST - Add classroom
    public Classroom addClassroom(Classroom classroom) {
        logger.info("Adding a new classroom");
        return classroomRepository.save(classroom);
    }

    // PUT - Update classroom
    public Classroom updateClassroom(Integer id, Classroom classroom) {
        logger.info("Updating classroom with ID: {}", id);

        Classroom existingClassroom =
                classroomRepository.findById(id).orElse(null);

        if (existingClassroom == null) {
            logger.warn("Classroom not found for update with ID: {}", id);
            return null;
        }

        existingClassroom.setRoomNumber(classroom.getRoomNumber());
        existingClassroom.setFloorNumber(classroom.getFloorNumber());
        existingClassroom.setCapacity(classroom.getCapacity());
        existingClassroom.setRoomType(classroom.getRoomType());
        existingClassroom.setStatus(classroom.getStatus());
        existingClassroom.setHasProjector(classroom.getHasProjector());
        existingClassroom.setHasSmartBoard(classroom.getHasSmartBoard());
        existingClassroom.setHasAC(classroom.getHasAC());

        Classroom updatedClassroom =
                classroomRepository.save(existingClassroom);

        logger.info("Classroom updated successfully with ID: {}", id);

        return updatedClassroom;
    }

    // DELETE - Delete classroom
    public void deleteClassroom(Integer id) {
        logger.info("Deleting classroom with ID: {}", id);

        if (!classroomRepository.existsById(id)) {
            logger.warn("Classroom not found for deletion with ID: {}", id);

            throw new RuntimeException(
                    "Classroom not found with ID: " + id
            );
        }

        classroomRepository.deleteById(id);

        logger.info("Classroom deleted successfully with ID: {}", id);
    }
}
