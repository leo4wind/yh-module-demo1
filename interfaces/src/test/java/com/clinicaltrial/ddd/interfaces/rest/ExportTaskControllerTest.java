package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.dataexport.application.service.ExportExecutionApplicationService;
import com.clinicaltrial.ddd.dataexport.application.service.ExportTaskApplicationService;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.repository.ExportTaskRepository;

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
class ExportTaskControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExportTaskRepository exportTaskRepository;

    @Mock
    private ExportTaskApplicationService exportTaskAppService;

    @Mock
    private ExportExecutionApplicationService exportExecAppService;

    @BeforeEach
    void setUp() {
        ExportTaskController controller = new ExportTaskController(
                exportTaskRepository, exportTaskAppService, exportExecAppService);
        mockMvc = standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // ---------------------------------------------------------------
    // GET /api/projects/{projectId}/export-tasks
    // ---------------------------------------------------------------

    @Test
    void listExportTasks_returnsList() throws Exception {
        ExportTask task = ControllerTestFixtures.anExportTask();
        when(exportTaskRepository.findByProjectId("1"))
                .thenReturn(Collections.singletonList(task));

        mockMvc.perform(get("/api/projects/1/export-tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].taskName").value("Test Export"))
                .andExpect(jsonPath("$.data[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.data[0].fileFormat").value("CSV"));
    }

    // ---------------------------------------------------------------
    // GET /api/export-tasks/{id}
    // ---------------------------------------------------------------

    @Test
    void getExportTask_returnsDetail() throws Exception {
        ExportTask task = ControllerTestFixtures.anExportTask();
        when(exportTaskRepository.getById(new ExportTaskId(1L))).thenReturn(task);

        mockMvc.perform(get("/api/export-tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.taskName").value("Test Export"))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andExpect(jsonPath("$.data.fileFormat").value("CSV"));
    }

    // ---------------------------------------------------------------
    // POST /api/export-tasks
    // ---------------------------------------------------------------

    @Test
    void createExportTask_returnsId() throws Exception {
        ExportTask task = ControllerTestFixtures.anExportTask();
        when(exportTaskAppService.createExportTask(any())).thenReturn(task);

        String body = "{"
                + "\"taskName\":\"Test Export\","
                + "\"projectId\":1,"
                + "\"fileFormat\":\"CSV\""
                + "}";

        mockMvc.perform(post("/api/export-tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/export-tasks/{id}/approve
    // ---------------------------------------------------------------

    @Test
    void approveExportTask_returnsId() throws Exception {
        ExportTask task = ControllerTestFixtures.anExportTask();
        when(exportTaskAppService.approveExportTask(any())).thenReturn(task);

        String body = "{"
                + "\"userId\":\"user1\","
                + "\"message\":\"Approved\""
                + "}";

        mockMvc.perform(post("/api/export-tasks/1/approve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ---------------------------------------------------------------
    // POST /api/export-tasks/{id}/submit
    // ---------------------------------------------------------------

    @Test
    void submitExportTask_returnsId() throws Exception {
        ExportTask task = ControllerTestFixtures.anExportTask();
        when(exportTaskAppService.submitExportTask(any())).thenReturn(task);

        mockMvc.perform(post("/api/export-tasks/1/submit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
