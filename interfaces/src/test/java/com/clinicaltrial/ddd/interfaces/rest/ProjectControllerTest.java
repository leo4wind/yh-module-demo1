package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.trial.application.service.CrfBindingApplicationService;
import com.clinicaltrial.ddd.trial.application.service.ProjectApplicationService;
import com.clinicaltrial.ddd.trial.application.service.SitePersonnelApplicationService;
import com.clinicaltrial.ddd.trial.application.service.StageApplicationService;
import com.clinicaltrial.ddd.trial.application.service.VisitPlanApplicationService;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectApplicationService projectAppService;

    @Mock
    private StageApplicationService stageAppService;

    @Mock
    private VisitPlanApplicationService visitPlanAppService;

    @Mock
    private CrfBindingApplicationService crfBindingAppService;

    @Mock
    private SitePersonnelApplicationService sitePersonnelAppService;

    @BeforeEach
    void setUp() {
        ProjectController controller = new ProjectController(
                projectRepository, projectAppService, stageAppService,
                visitPlanAppService, crfBindingAppService, sitePersonnelAppService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------------
    // GET /api/projects/{id}
    // ---------------------------------------------------------------

    @Test
    void getProject_returnsProjectDetail() throws Exception {
        Project project = ControllerTestFixtures.aProject();
        when(projectRepository.getById(new ProjectId(1L))).thenReturn(project);

        mockMvc.perform(get("/api/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Test Project"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"));
    }

    @Test
    void getProject_notFound_returns404() throws Exception {
        when(projectRepository.getById(new ProjectId(999L)))
                .thenThrow(new AggregateNotFoundException("Project", 999L));

        mockMvc.perform(get("/api/projects/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    // ---------------------------------------------------------------
    // GET /api/projects
    // ---------------------------------------------------------------

    @Test
    void listProjects_returnsPage() throws Exception {
        Project project = ControllerTestFixtures.aProject();
        when(projectRepository.findAll(anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(project));

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].title").value("Test Project"))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    void listProjects_withStatusFilter() throws Exception {
        Project project = ControllerTestFixtures.aProject();
        when(projectRepository.findByStatus(ProjectStatus.DRAFT))
                .thenReturn(Collections.singletonList(project));

        mockMvc.perform(get("/api/projects?status=DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/projects
    // ---------------------------------------------------------------

    @Test
    void createProject_returnsId() throws Exception {
        Project project = ControllerTestFixtures.aProject();
        when(projectAppService.createProject(any())).thenReturn(project);

        String body = "{\"title\":\"Test Project\",\"type\":\"INTERVENTIONAL\","
                + "\"abbreviation\":\"TP\",\"prefix\":\"TT\",\"openScreen\":true,"
                + "\"expectedSubjectSize\":100,\"clinicalNumber\":\"CHN-001\","
                + "\"registrationNo\":\"REG-001\",\"purpose\":\"Test purpose\","
                + "\"createUserId\":\"user1\"}";

        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/projects/{id}/activate
    // ---------------------------------------------------------------

    @Test
    void activateProject_returns200() throws Exception {
        when(projectAppService.activateProject(any())).thenReturn(ControllerTestFixtures.aProject());

        mockMvc.perform(post("/api/projects/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/projects/{id}/close
    // ---------------------------------------------------------------

    @Test
    void closeProject_returns200() throws Exception {
        when(projectAppService.closeProject(any())).thenReturn(ControllerTestFixtures.aProject());

        mockMvc.perform(post("/api/projects/1/close"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/projects/{id}/stages
    // ---------------------------------------------------------------

    @Test
    void addStage_returns200() throws Exception {
        Project project = ControllerTestFixtures.aProject();
        when(stageAppService.addStage(any())).thenReturn(project);

        String body = "{\"name\":\"Screening\",\"repeatType\":\"NONE\",\"autoAdd\":false}";

        mockMvc.perform(post("/api/projects/1/stages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
