package com.university.erp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.university.erp.examination.repository.ExaminationRepository;
import com.university.erp.examination.repository.GradeRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:erp_test;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
class ExaminationGradingApiTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @Autowired
    private ExaminationRepository examinationRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @BeforeEach
    void cleanDatabase() {
        gradeRepository.deleteAll();
        examinationRepository.deleteAll();
    }

    @Test
    void examinationApiSupportsCrudAndSubjectFilter() throws Exception {
        MvcResult created = mockMvc.perform(post("/api/examinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(examinationRequest("SCHEDULED", 100)))
                .andExpect(status().isCreated()).andReturn();
        long examId = read(created.getResponse().getContentAsString()).get("examId").asLong();

        MvcResult filtered = mockMvc.perform(get("/api/examinations").param("subjectId", "42"))
                .andExpect(status().isOk()).andReturn();
        assertEquals(1, objectMapper.readTree(filtered.getResponse().getContentAsString()).size());

        MvcResult updated = mockMvc.perform(put("/api/examinations/{examId}", examId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(examinationRequest("PUBLISHED", 100)))
                .andExpect(status().isOk()).andReturn();
        assertEquals("PUBLISHED", read(updated.getResponse().getContentAsString()).get("status").asText());

        mockMvc.perform(delete("/api/examinations/{examId}", examId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/examinations/{examId}", examId)).andExpect(status().isNotFound());
    }

    @Test
    void gradeApiFiltersRejectsDuplicateAndOutOfRangeMarksAndDeletes() throws Exception {
        MvcResult exam = mockMvc.perform(post("/api/examinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(examinationRequest("SCHEDULED", 100)))
                .andExpect(status().isCreated()).andReturn();
        long examId = read(exam.getResponse().getContentAsString()).get("examId").asLong();
        UUID studentId = UUID.randomUUID();
        String gradeBody = gradeRequest(examId, studentId, 88);

        MvcResult created = mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON).content(gradeBody))
                .andExpect(status().isCreated()).andReturn();
        long gradingId = read(created.getResponse().getContentAsString()).get("gradingId").asLong();

        mockMvc.perform(post("/api/grades").contentType(MediaType.APPLICATION_JSON).content(gradeBody))
                .andExpect(status().isConflict());
        mockMvc.perform(post("/api/grades").contentType(MediaType.APPLICATION_JSON)
                        .content(gradeRequest(examId, UUID.randomUUID(), 101)))
                .andExpect(status().isBadRequest());

        MvcResult filtered = mockMvc.perform(get("/api/grades")
                        .param("examId", String.valueOf(examId)).param("studentId", studentId.toString()))
                .andExpect(status().isOk()).andReturn();
        assertEquals(1, objectMapper.readTree(filtered.getResponse().getContentAsString()).size());

        mockMvc.perform(delete("/api/grades/{gradingId}", gradingId)).andExpect(status().isNoContent());
        mockMvc.perform(get("/api/grades/{gradingId}", gradingId)).andExpect(status().isNotFound());
    }

    private JsonNode read(String body) throws Exception {
        assertNotNull(body);
        return objectMapper.readTree(body);
    }

    private String examinationRequest(String status, int maxMarks) {
        return """
                { "subjectId": 42, "academicYear": "2025-2026", "semester": 1,
                  "examType": "FINAL", "maxMarks": %d, "examDate": "2026-11-01",
                  "status": "%s", "remarks": "test" }
                """.formatted(maxMarks, status);
    }

    private String gradeRequest(long examId, UUID studentId, int marks) {
        return """
                { "examId": %d, "studentId": "%s", "marksObtained": %d,
                  "grade": "A", "gradePoint": 4.0, "resultStatus": "PASS" }
                """.formatted(examId, studentId, marks);
    }
}
