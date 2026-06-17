package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.statistics.application.service.AnalysisExecutionApplicationService;
import com.clinicaltrial.ddd.statistics.application.service.AnalysisProjectApplicationService;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnalysisProjectRepository analysisProjectRepository;

    @Mock
    private AnalysisProjectApplicationService analysisProjectAppService;

    @Mock
    private AnalysisExecutionApplicationService analysisExecAppService;

    @BeforeEach
    void setUp() {
        AnalysisController controller = new AnalysisController(
                analysisProjectRepository, analysisProjectAppService, analysisExecAppService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------------
    // GET /api/analysis/projects
    // ---------------------------------------------------------------

    @Test
    void listAnalysisProjects_returnsList() throws Exception {
        AnalysisProject project = ControllerTestFixtures.anAnalysisProject();
        when(analysisProjectRepository.findAll())
                .thenReturn(Collections.singletonList(project));

        mockMvc.perform(get("/api/analysis/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Test Analysis"))
                .andExpect(jsonPath("$.data[0].description").value("Description"))
                .andExpect(jsonPath("$.data[0].variables").isArray())
                .andExpect(jsonPath("$.data[0].analysisConfigs").isArray())
                .andExpect(jsonPath("$.data[0].results").isArray());
    }

    // ---------------------------------------------------------------
    // GET /api/analysis/projects/{id}
    // ---------------------------------------------------------------

    @Test
    void getAnalysisProject_returnsDetail() throws Exception {
        AnalysisProject project = ControllerTestFixtures.anAnalysisProject();
        when(analysisProjectRepository.getById(new AnalysisProjectId(1L))).thenReturn(project);

        mockMvc.perform(get("/api/analysis/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Test Analysis"))
                .andExpect(jsonPath("$.data.description").value("Description"))
                .andExpect(jsonPath("$.data.variables").isArray())
                .andExpect(jsonPath("$.data.analysisConfigs").isArray())
                .andExpect(jsonPath("$.data.results").isArray());
    }

    // ---------------------------------------------------------------
    // POST /api/analysis/projects
    // ---------------------------------------------------------------

    @Test
    void createAnalysisProject_returnsId() throws Exception {
        AnalysisProject project = ControllerTestFixtures.anAnalysisProject();
        when(analysisProjectAppService.createProject(anyString(), anyString())).thenReturn(project);

        String body = "{"
                + "\"name\":\"Test Analysis\","
                + "\"description\":\"Description\""
                + "}";

        mockMvc.perform(post("/api/analysis/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
