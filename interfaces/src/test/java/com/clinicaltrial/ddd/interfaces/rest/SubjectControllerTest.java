package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.subject.application.service.SubjectApplicationService;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.repository.SubjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class SubjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SubjectApplicationService subjectAppService;

    @BeforeEach
    void setUp() {
        SubjectController controller = new SubjectController(subjectRepository, subjectAppService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------------
    // GET /api/subjects/{id}
    // ---------------------------------------------------------------

    @Test
    void getSubject_returnsDetail() throws Exception {
        Subject subject = ControllerTestFixtures.aSubject();
        when(subjectRepository.getById(new SubjectId(1L))).thenReturn(subject);

        mockMvc.perform(get("/api/subjects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.code").value("TT-0001"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    void getSubject_notFound_returns404() throws Exception {
        when(subjectRepository.getById(new SubjectId(999L)))
                .thenThrow(new AggregateNotFoundException("Subject", 999L));

        mockMvc.perform(get("/api/subjects/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ---------------------------------------------------------------
    // GET /api/projects/{projectId}/subjects
    // ---------------------------------------------------------------

    @Test
    void listSubjectsByProject_returnsList() throws Exception {
        Subject subject = ControllerTestFixtures.aSubject();
        when(subjectRepository.findByProjectId(new ProjectId(1L)))
                .thenReturn(Collections.singletonList(subject));

        mockMvc.perform(get("/api/projects/1/subjects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].code").value("TT-0001"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
    }

    // ---------------------------------------------------------------
    // POST /api/subjects/screen
    // ---------------------------------------------------------------

    @Test
    void screenSubject_createsAndReturnsId() throws Exception {
        Subject subject = ControllerTestFixtures.aSubject();
        when(subjectAppService.screenSubject(any())).thenReturn(subject);

        String body = "{"
                + "\"projectId\":1,"
                + "\"siteId\":100,"
                + "\"userId\":200,"
                + "\"screeningDate\":\"2024-01-15\","
                + "\"screeningResult\":\"PASS\","
                + "\"remarks\":\"All criteria met\","
                + "\"blh\":\"BLH-001\","
                + "\"syxh\":\"SYXH-001\""
                + "}";

        mockMvc.perform(post("/api/subjects/screen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/subjects/{id}/enroll
    // ---------------------------------------------------------------

    @Test
    void enrollSubject_returns200() throws Exception {
        Subject subject = ControllerTestFixtures.aSubject();
        when(subjectAppService.enrollSubject(any())).thenReturn(subject);

        String body = "{"
                + "\"projectId\":1,"
                + "\"siteId\":100,"
                + "\"userId\":200,"
                + "\"blh\":\"BLH-001\","
                + "\"syxh\":\"SYXH-001\","
                + "\"groupSubsetIds\":[]"
                + "}";

        mockMvc.perform(post("/api/subjects/1/enroll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/subjects/{id}/withdraw
    // ---------------------------------------------------------------

    @Test
    void withdrawSubject_returns200() throws Exception {
        Subject subject = ControllerTestFixtures.aSubject();
        when(subjectAppService.withdrawSubject(any())).thenReturn(subject);

        String body = "{"
                + "\"reasonCode\":\"WITHDRAWAL_OF_CONSENT\","
                + "\"reasonDescription\":\"Subject withdrew consent\""
                + "}";

        mockMvc.perform(post("/api/subjects/1/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
