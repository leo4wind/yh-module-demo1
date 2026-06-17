package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.application.service.QueryApplicationService;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;

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
class QueryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QueryRepository queryRepository;

    @Mock
    private QueryApplicationService queryAppService;

    @BeforeEach
    void setUp() {
        QueryController controller = new QueryController(queryRepository, queryAppService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------------
    // GET /api/assessments/{assessmentId}/queries
    // ---------------------------------------------------------------

    @Test
    void listQueriesByAssessment_returnsList() throws Exception {
        Query query = ControllerTestFixtures.aQuery();
        when(queryRepository.findByAssessmentId(new CrfAssessmentId(1L)))
                .thenReturn(Collections.singletonList(query));

        mockMvc.perform(get("/api/assessments/1/queries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].assessmentId").value(1))
                .andExpect(jsonPath("$.data[0].status").value("OPEN"))
                .andExpect(jsonPath("$.data[0].question").value("Data discrepancy?"));
    }

    // ---------------------------------------------------------------
    // GET /api/queries/{id}
    // ---------------------------------------------------------------

    @Test
    void getQuery_returnsDetail() throws Exception {
        Query query = ControllerTestFixtures.aQuery();
        when(queryRepository.getById(new QueryId(1L))).thenReturn(query);

        mockMvc.perform(get("/api/queries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.assessmentId").value(1))
                .andExpect(jsonPath("$.data.fieldCode").value("btbm00"))
                .andExpect(jsonPath("$.data.status").value("OPEN"))
                .andExpect(jsonPath("$.data.question").value("Data discrepancy?"));
    }

    // ---------------------------------------------------------------
    // POST /api/queries
    // ---------------------------------------------------------------

    @Test
    void raiseQuery_returnsId() throws Exception {
        Query query = ControllerTestFixtures.aQuery();
        when(queryAppService.raiseQuery(any())).thenReturn(query);

        String body = "{"
                + "\"assessmentId\":1,"
                + "\"fieldCode\":\"btbm00\","
                + "\"subTableId\":\"\","
                + "\"fieldType\":\"TEXT\","
                + "\"question\":\"Data discrepancy?\","
                + "\"originalFieldCode\":\"btbm00\","
                + "\"originalFieldValue\":\"old\","
                + "\"originalFieldValueText\":\"Old Value\","
                + "\"userId\":100"
                + "}";

        mockMvc.perform(post("/api/queries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/queries/{id}/respond
    // ---------------------------------------------------------------

    @Test
    void respondToQuery_returnsId() throws Exception {
        Query query = ControllerTestFixtures.aQuery();
        when(queryAppService.respondToQuery(any())).thenReturn(query);

        String body = "{"
                + "\"response\":\"Data verified correct\","
                + "\"updateType\":\"CLARIFY_ONLY\","
                + "\"userId\":100"
                + "}";

        mockMvc.perform(post("/api/queries/1/respond")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/queries/{id}/close
    // ---------------------------------------------------------------

    @Test
    void closeQuery_returnsId() throws Exception {
        Query query = ControllerTestFixtures.aQuery();
        when(queryAppService.closeQuery(any())).thenReturn(query);

        String body = "{"
                + "\"userId\":100"
                + "}";

        mockMvc.perform(post("/api/queries/1/close")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
