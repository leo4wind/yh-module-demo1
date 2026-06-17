package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.datacollection.application.service.CrfFillingApplicationService;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.datacollection.domain.repository.SubjectStageRepository;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class CrfAssessmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SubjectStageRepository subjectStageRepository;

    @Mock
    private CrfAssessmentRepository crfAssessmentRepository;

    @Mock
    private CrfFillingApplicationService crfFillingAppService;

    @BeforeEach
    void setUp() {
        CrfAssessmentController controller = new CrfAssessmentController(
                subjectStageRepository, crfAssessmentRepository, crfFillingAppService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------------
    // GET /api/subjects/{subjectId}/stages
    // ---------------------------------------------------------------

    @Test
    void getSubjectStages_returnsList() throws Exception {
        SubjectStage stage = ControllerTestFixtures.aSubjectStage();
        when(subjectStageRepository.findBySubjectId(new SubjectId(1L)))
                .thenReturn(Collections.singletonList(stage));

        mockMvc.perform(get("/api/subjects/1/stages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].subjectId").value(10))
                .andExpect(jsonPath("$.data[0].stageId").value(100))
                .andExpect(jsonPath("$.data[0].status").value("IN_PROGRESS"));
    }

    // ---------------------------------------------------------------
    // GET /api/stages/{id}
    // ---------------------------------------------------------------

    @Test
    void getStage_returnsStageWithAssessments() throws Exception {
        SubjectStage stage = ControllerTestFixtures.aSubjectStage();
        CrfAssessment assessment = ControllerTestFixtures.aCrfAssessment();

        when(subjectStageRepository.getById(new SubjectStageId(1L))).thenReturn(stage);
        when(crfAssessmentRepository.findBySubjectsStageId(new SubjectStageId(1L)))
                .thenReturn(Collections.singletonList(assessment));

        mockMvc.perform(get("/api/stages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.stageId").value(100))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.crfAssessments").isArray())
                .andExpect(jsonPath("$.data.crfAssessments[0].id").value(1))
                .andExpect(jsonPath("$.data.crfAssessments[0].status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.crfAssessments[0].completeness").value(100.0));
    }

    // ---------------------------------------------------------------
    // GET /api/assessments/{id}
    // ---------------------------------------------------------------

    @Test
    void getAssessment_returnsDetailWithFieldValues() throws Exception {
        CrfAssessment assessment = ControllerTestFixtures.aCrfAssessmentWithFieldValues();
        when(crfAssessmentRepository.getById(new CrfAssessmentId(2L))).thenReturn(assessment);

        mockMvc.perform(get("/api/assessments/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data.completeness").value(50.0))
                .andExpect(jsonPath("$.data.fieldValues").isArray())
                .andExpect(jsonPath("$.data.fieldValues[0].fieldCode").value("btbm00"))
                .andExpect(jsonPath("$.data.fieldValues[0].fieldValue").value("value1"))
                .andExpect(jsonPath("$.data.fieldValues[1].fieldCode").value("dabm00"))
                .andExpect(jsonPath("$.data.fieldValues[1].fieldValue").value("value2"));
    }

    // ---------------------------------------------------------------
    // POST /api/assessments/{id}/field-values
    // ---------------------------------------------------------------

    @Test
    void saveFieldValue_returns200() throws Exception {
        String body = "{"
                + "\"fieldCode\":\"btbm00\","
                + "\"fieldLabel\":\"btms00\","
                + "\"fieldValue\":\"val\","
                + "\"fieldValueText\":\"valText\","
                + "\"dataUnit\":\"kg\","
                + "\"fieldType\":\"text\","
                + "\"userId\":100"
                + "}";

        mockMvc.perform(post("/api/assessments/1/field-values")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}
