package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.dataexport.application.command.ApproveExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.application.command.CreateExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.application.command.ExecuteExportCommand;
import com.clinicaltrial.ddd.dataexport.application.command.RejectExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.application.command.SubmitExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.application.service.ExportExecutionApplicationService;
import com.clinicaltrial.ddd.dataexport.application.service.ExportTaskApplicationService;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.FileFormat;
import com.clinicaltrial.ddd.dataexport.domain.repository.ExportTaskRepository;
import com.clinicaltrial.ddd.interfaces.dto.ApiResponse;
import com.clinicaltrial.ddd.interfaces.dto.IdResponse;
import com.clinicaltrial.ddd.interfaces.dto.request.ApproveExportTaskRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.CreateExportTaskRequest;
import com.clinicaltrial.ddd.interfaces.dto.request.RejectExportTaskRequest;
import com.clinicaltrial.ddd.interfaces.dto.response.ExportTaskResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for BC5: Data Export (数据导出审批).
 */
@RestController
public class ExportTaskController {

    private final ExportTaskRepository exportTaskRepository;
    private final ExportTaskApplicationService exportTaskAppService;
    private final ExportExecutionApplicationService exportExecAppService;

    public ExportTaskController(ExportTaskRepository exportTaskRepository,
                                ExportTaskApplicationService exportTaskAppService,
                                ExportExecutionApplicationService exportExecAppService) {
        this.exportTaskRepository = exportTaskRepository;
        this.exportTaskAppService = exportTaskAppService;
        this.exportExecAppService = exportExecAppService;
    }

    /** 导出任务列表. */
    @GetMapping("/api/projects/{projectId}/export-tasks")
    public ApiResponse<List<ExportTaskResponse>> listExportTasks(@PathVariable Long projectId) {
        List<ExportTask> tasks = exportTaskRepository.findByProjectId(projectId.toString());
        List<ExportTaskResponse> result = tasks.stream()
                .map(this::toExportTaskResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    /** 所有导出任务列表. */
    @GetMapping("/api/export-tasks")
    public ApiResponse<List<ExportTaskResponse>> listAllExportTasks() {
        List<ExportTask> tasks = exportTaskRepository.findAll();
        List<ExportTaskResponse> result = tasks.stream()
                .map(this::toExportTaskResponse)
                .collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    /** 导出任务详情. */
    @GetMapping("/api/export-tasks/{id}")
    public ApiResponse<ExportTaskResponse> getExportTask(@PathVariable Long id) {
        ExportTask task = exportTaskRepository.getById(new ExportTaskId(id));
        return ApiResponse.success(toExportTaskResponse(task));
    }

    /** 创建导出任务. */
    @PostMapping("/api/export-tasks")
    public ApiResponse<IdResponse> createExportTask(@RequestBody CreateExportTaskRequest req) {
        FileFormat fileFormat = req.getFileFormat() != null
                ? FileFormat.valueOf(req.getFileFormat()) : null;
        CreateExportTaskCommand cmd = new CreateExportTaskCommand(
                req.getTaskName(),
                req.getProjectId() != null ? req.getProjectId().toString() : null,
                req.getStageId() != null ? req.getStageId().toString() : null,
                req.getCrfVersionId() != null ? req.getCrfVersionId().toString() : null,
                fileFormat);
        ExportTask task = exportTaskAppService.createExportTask(cmd);
        return ApiResponse.success(new IdResponse(task.getId().getValue()));
    }

    /** 提交审批. */
    @PostMapping("/api/export-tasks/{id}/submit")
    public ApiResponse<IdResponse> submitExportTask(@PathVariable Long id) {
        SubmitExportTaskCommand cmd = new SubmitExportTaskCommand(new ExportTaskId(id));
        ExportTask task = exportTaskAppService.submitExportTask(cmd);
        return ApiResponse.success(new IdResponse(task.getId().getValue()));
    }

    /** 审核通过. */
    @PostMapping("/api/export-tasks/{id}/approve")
    public ApiResponse<IdResponse> approveExportTask(@PathVariable Long id,
                                                      @RequestBody ApproveExportTaskRequest req) {
        ApproveExportTaskCommand cmd = new ApproveExportTaskCommand(
                new ExportTaskId(id), req.getUserId(), req.getMessage());
        ExportTask task = exportTaskAppService.approveExportTask(cmd);
        return ApiResponse.success(new IdResponse(task.getId().getValue()));
    }

    /** 审核驳回. */
    @PostMapping("/api/export-tasks/{id}/reject")
    public ApiResponse<IdResponse> rejectExportTask(@PathVariable Long id,
                                                     @RequestBody RejectExportTaskRequest req) {
        RejectExportTaskCommand cmd = new RejectExportTaskCommand(
                new ExportTaskId(id), req.getUserId(), req.getReason());
        ExportTask task = exportTaskAppService.rejectExportTask(cmd);
        return ApiResponse.success(new IdResponse(task.getId().getValue()));
    }

    /** 执行导出. */
    @PostMapping("/api/export-tasks/{id}/execute")
    public ApiResponse<IdResponse> executeExport(@PathVariable Long id) {
        ExecuteExportCommand cmd = new ExecuteExportCommand(new ExportTaskId(id));
        ExportTask task = exportExecAppService.executeExport(cmd);
        return ApiResponse.success(new IdResponse(task.getId().getValue()));
    }

    // ========== 转换方法 ==========

    private ExportTaskResponse toExportTaskResponse(ExportTask t) {
        ExportTaskResponse r = new ExportTaskResponse();
        r.setId(t.getId().getValue());
        r.setTaskName(t.getTaskName());
        r.setProjectId(t.getProjectId() != null ? t.getProjectId().getValue() : null);
        r.setStageId(t.getStageId() != null ? t.getStageId().getValue() : null);
        r.setCrfVersionId(t.getCrfVersionId() != null ? t.getCrfVersionId().getValue() : null);
        r.setStatus(t.getStatus() != null ? t.getStatus().name() : null);
        r.setFileFormat(t.getFileFormat() != null ? t.getFileFormat().name() : null);
        r.setAuditUserId(t.getAuditUserId());
        r.setAuditTime(t.getAuditTime());
        r.setAuditMessage(t.getAuditMessage());
        r.setFileUrl(t.getFileUrl());
        r.setFileName(t.getFileName());
        r.setDownloadCount(t.getDownloadCount());
        r.setFailCount(t.getFailCount());
        r.setFailMessage(t.getFailMessage());
        return r;
    }
}
