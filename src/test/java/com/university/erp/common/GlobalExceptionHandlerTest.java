package com.university.erp.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void preservesStatusAndReasonFromResponseStatusException() {
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResponseStatusException(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Student not found", response.getBody().get("error"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void mapsDatabaseConstraintFailuresToConflict() {
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataIntegrityViolation(
                new DataIntegrityViolationException("database detail"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(
                "Request conflicts with existing data or violates a database constraint",
                response.getBody().get("error"));
    }

    @Test
    void hidesUnexpectedExceptionDetailsFromResponse() {
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUnexpectedException(
                new RuntimeException("internal detail"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", response.getBody().get("error"));
    }
}
