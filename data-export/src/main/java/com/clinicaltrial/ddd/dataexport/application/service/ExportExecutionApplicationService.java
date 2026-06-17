package com.clinicaltrial.ddd.dataexport.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.dataexport.application.command.ExecuteExportCommand;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportResult;
import com.clinicaltrial.ddd.dataexport.domain.repository.ExportTaskRepository;
import com.clinicaltrial.ddd.dataexport.domain.service.ExportExecutionService;
import com.clinicaltrial.ddd.dataexport.domain.service.ExportRetryService;

import java.util.Objects;

/**
 * ExportExecutionApplicationService — 导出执行应用服务.
 * <p>
 * 编排导出执行的核心用例：执行导出、重试导出。
 * 应用服务仅负责协调和编排，不包含业务逻辑——所有业务规则由领域层执行。
 * </p>
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>接收应用指令并转换为领域操作</li>
 *   <li>加载和保存聚合</li>
 *   <li>协调导出执行流程</li>
 *   <li>发布领域事件</li>
 * </ul>
 */
@Service
public class ExportExecutionApplicationService {

    private final ExportTaskRepository exportTaskRepository;
    private final ExportExecutionService exportExecutionService;
    private final ExportRetryService exportRetryService;
    private final EventBus eventBus;

    /**
     * 构造ExportExecutionApplicationService.
     *
     * @param exportTaskRepository   ExportTask仓储
     * @param exportExecutionService 导出执行领域服务
     * @param exportRetryService     导出重试领域服务
     * @param eventBus               事件总线
     */
    public ExportExecutionApplicationService(ExportTaskRepository exportTaskRepository,
                                              ExportExecutionService exportExecutionService,
                                              ExportRetryService exportRetryService,
                                              EventBus eventBus) {
        this.exportTaskRepository = exportTaskRepository;
        this.exportExecutionService = exportExecutionService;
        this.exportRetryService = exportRetryService;
        this.eventBus = eventBus;
    }

    /**
     * 执行导出.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载ExportTask聚合</li>
     *   <li>调用 {@link ExportTask#markExporting()} 标记为导出中</li>
     *   <li>调用领域服务执行实际导出</li>
     *   <li>根据执行结果调用 markCompleted 或 markFailed</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 执行导出指令
     * @return 更新后的ExportTask实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果状态不允许执行
     */
    public ExportTask executeExport(ExecuteExportCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load ExportTask aggregate
        ExportTask exportTask = exportTaskRepository.getById(command.getTaskId());

        // Step 2: Mark as exporting (validates state transition)
        exportTask.markExporting();

        try {
            // Step 3: Execute actual export via domain service
            ExportResult result = exportExecutionService.executeExport(exportTask);

            // Step 4: Mark as completed
            exportTask.markCompleted(result.getFileUrl());
        } catch (Exception e) {
            // Step 4 (failure path): Mark as failed
            exportTask.markFailed(e.getMessage());
        }

        // Step 5: Persist
        ExportTask savedTask = exportTaskRepository.save(exportTask);

        // Step 6: Publish domain events
        eventBus.publishAll(savedTask);

        return savedTask;
    }

    /**
     * 重试失败的导出任务.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载ExportTask聚合</li>
     *   <li>调用领域服务校验是否允许重试</li>
     *   <li>调用 {@link ExportTask#markExporting()} 从FAILED转为EXPORTING</li>
     *   <li>执行导出并更新状态</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 执行导出指令（复用）
     * @return 更新后的ExportTask实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果已达到最大重试次数
     */
    public ExportTask retryExport(ExecuteExportCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load ExportTask aggregate
        ExportTask exportTask = exportTaskRepository.getById(command.getTaskId());

        // Step 2: Validate retry possibility via domain service
        exportRetryService.prepareRetry(exportTask);

        // Step 3: Mark as exporting (FAILED -> EXPORTING)
        exportTask.markExporting();

        try {
            // Step 4: Execute actual export via domain service
            ExportResult result = exportExecutionService.executeExport(exportTask);

            // Step 5: Mark as completed
            exportTask.markCompleted(result.getFileUrl());
        } catch (Exception e) {
            // Step 5 (failure path): Mark as failed
            exportTask.markFailed(e.getMessage());
        }

        // Step 6: Persist
        ExportTask savedTask = exportTaskRepository.save(exportTask);

        // Step 7: Publish domain events
        eventBus.publishAll(savedTask);

        return savedTask;
    }
}
