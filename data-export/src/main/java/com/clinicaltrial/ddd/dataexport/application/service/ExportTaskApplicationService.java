package com.clinicaltrial.ddd.dataexport.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.dataexport.application.command.ApproveExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.application.command.CreateExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.application.command.RejectExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.application.command.SubmitExportTaskCommand;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.repository.ExportTaskRepository;
import com.clinicaltrial.ddd.dataexport.domain.service.ExportDuplicateDetectionService;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import java.util.Objects;

/**
 * ExportTaskApplicationService — 导出任务管理应用服务.
 * <p>
 * 编排导出任务管理的核心用例：创建任务、提交审批、审批通过、驳回审批。
 * 应用服务仅负责协调和编排，不包含业务逻辑——所有业务规则由领域层（聚合、领域服务）执行。
 * </p>
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>接收应用指令并转换为领域操作</li>
 *   <li>校验输入参数的完整性</li>
 *   <li>调用领域服务（如重复检测）</li>
 *   <li>加载和保存聚合</li>
 *   <li>发布领域事件</li>
 * </ul>
 */
@Service
public class ExportTaskApplicationService {

    private final ExportTaskRepository exportTaskRepository;
    private final ExportDuplicateDetectionService duplicateDetectionService;
    private final EventBus eventBus;

    /**
     * 构造ExportTaskApplicationService.
     *
     * @param exportTaskRepository     ExportTask仓储
     * @param duplicateDetectionService 导出重复检测领域服务
     * @param eventBus                  事件总线
     */
    public ExportTaskApplicationService(ExportTaskRepository exportTaskRepository,
                                         ExportDuplicateDetectionService duplicateDetectionService,
                                         EventBus eventBus) {
        this.exportTaskRepository = exportTaskRepository;
        this.duplicateDetectionService = duplicateDetectionService;
        this.eventBus = eventBus;
    }

    /**
     * 创建导出任务.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>校验任务名称在项目中是否唯一</li>
     *   <li>通过工厂方法创建ExportTask聚合（状态：DRAFT）</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 创建导出任务指令
     * @return 创建的ExportTask实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果任务名称在项目中已存在
     */
    public ExportTask createExportTask(CreateExportTaskCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Validate task name uniqueness within project
        duplicateDetectionService.validateTaskNameUnique(
                command.getTaskName(), command.getProjectId());

        // Step 2: Build cross-BC reference value objects
        ProjectId projectId = new ProjectId(Long.valueOf(command.getProjectId()));
        StageId stageId = command.getStageId() != null
                ? new StageId(Long.valueOf(command.getStageId())) : null;
        CrfVersionId crfVersionId = command.getCrfVersionId() != null
                ? new CrfVersionId(Long.valueOf(command.getCrfVersionId())) : null;

        // Step 3: Create ExportTask aggregate via factory method
        ExportTaskId taskId = generateExportTaskId();
        ExportTask exportTask = ExportTask.create(
                taskId,
                command.getTaskName(),
                projectId,
                stageId,
                crfVersionId,
                command.getFileFormat()
        );

        // Step 4: Persist
        ExportTask savedTask = exportTaskRepository.save(exportTask);

        // Step 5: Publish domain events
        eventBus.publishAll(savedTask);

        return savedTask;
    }

    /**
     * 提交导出任务审批.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载ExportTask聚合</li>
     *   <li>调用 {@link ExportTask#submit()} 方法（状态：DRAFT → PENDING_APPROVAL）</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 提交审批指令
     * @return 更新后的ExportTask实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果状态不允许提交
     */
    public ExportTask submitExportTask(SubmitExportTaskCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load ExportTask aggregate
        ExportTask exportTask = exportTaskRepository.getById(command.getTaskId());

        // Step 2: Call domain method (validates state transition DRAFT -> PENDING_APPROVAL)
        exportTask.submit();

        // Step 3: Persist
        ExportTask savedTask = exportTaskRepository.save(exportTask);

        // Step 4: Publish domain events
        eventBus.publishAll(savedTask);

        return savedTask;
    }

    /**
     * 审批通过导出任务.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载ExportTask聚合</li>
     *   <li>调用 {@link ExportTask#approve(String, String)} 方法</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 审批通过指令
     * @return 更新后的ExportTask实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果状态不允许审批
     */
    public ExportTask approveExportTask(ApproveExportTaskCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load ExportTask aggregate
        ExportTask exportTask = exportTaskRepository.getById(command.getTaskId());

        // Step 2: Call domain method (validates state transition PENDING_APPROVAL -> APPROVED)
        exportTask.approve(command.getUserId(), command.getMessage());

        // Step 3: Persist
        ExportTask savedTask = exportTaskRepository.save(exportTask);

        // Step 4: Publish domain events
        eventBus.publishAll(savedTask);

        return savedTask;
    }

    /**
     * 驳回导出任务.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载ExportTask聚合</li>
     *   <li>调用 {@link ExportTask#reject(String, String)} 方法</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 驳回指令
     * @return 更新后的ExportTask实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果状态不允许驳回
     */
    public ExportTask rejectExportTask(RejectExportTaskCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load ExportTask aggregate
        ExportTask exportTask = exportTaskRepository.getById(command.getTaskId());

        // Step 2: Call domain method (validates state transition PENDING_APPROVAL -> DRAFT)
        exportTask.reject(command.getUserId(), command.getReason());

        // Step 3: Persist
        ExportTask savedTask = exportTaskRepository.save(exportTask);

        // Step 4: Publish domain events
        eventBus.publishAll(savedTask);

        return savedTask;
    }

    /**
     * 生成导出任务ID.
     * <p>
     * 当前使用系统时间戳作为ID生成策略的占位实现。
     * 生产环境应替换为分布式ID生成器（如雪花算法或数据库序列）。
     * </p>
     *
     * @return 新的ExportTaskId
     */
    private ExportTaskId generateExportTaskId() {
        // TODO: Replace with proper ID generation strategy
        return new ExportTaskId(System.currentTimeMillis());
    }
}
