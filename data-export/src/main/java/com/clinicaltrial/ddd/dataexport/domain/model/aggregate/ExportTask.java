package com.clinicaltrial.ddd.dataexport.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportExecutionCompletedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportExecutionFailedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportExecutionStartedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportTaskApprovedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportTaskRejectedEvent;
import com.clinicaltrial.ddd.dataexport.domain.event.ExportTaskSubmittedEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportExecutionLog;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFieldConfig;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFilter;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFieldConfigId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFilterId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportStatus;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.FileFormat;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * ExportTask — 数据导出任务聚合根.
 * <p>
 * 表示一条数据导出请求，包含导出范围定义（项目、阶段、CRF版本）、
 * 字段配置、筛选条件和执行状态。
 * 导出任务需要经过审批后才能执行，支持失败重试。
 * </p>
 *
 * <h3>状态机</h3>
 * <pre>
 * DRAFT ──→ PENDING_APPROVAL ──→ APPROVED ──→ EXPORTING ──→ COMPLETED
 *   ↑              │                             │
 *   └── reject ────┘                             └──→ FAILED ──→ EXPORTING (retry)
 * </pre>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>只有草稿状态的导出任务可以被修改</li>
 *   <li>只有草稿状态的导出任务可以提交审批</li>
 *   <li>只有待审批状态的导出任务可以被审批通过或驳回</li>
 *   <li>只有已批准或失败状态的导出任务可以开始执行</li>
 *   <li>失败重试最多3次</li>
 * </ul>
 *
 * @see ExportStatus
 * @see FileFormat
 */
public class ExportTask extends AggregateRoot<ExportTaskId> {

    private static final int MAX_RETRY_COUNT = 3;

    private ExportTaskId id;
    private String taskName;
    private ProjectId projectId;
    private StageId stageId;
    private CrfVersionId crfVersionId;
    private ExportStatus status;
    private FileFormat fileFormat;
    private String auditUserId;
    private Date auditTime;
    private String auditMessage;
    private String fileUrl;
    private String fileName;
    private Integer downloadCount;
    private Integer failCount;
    private String failMessage;
    private List<ExportFieldConfig> fieldConfigs;
    private List<ExportFilter> filters;
    private List<ExportExecutionLog> executionLogs;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private ExportTask() {
        this.fieldConfigs = new ArrayList<>();
        this.filters = new ArrayList<>();
        this.executionLogs = new ArrayList<>();
        this.downloadCount = 0;
        this.failCount = 0;
    }

    // ========== 工厂方法 ==========

    /**
     * 创建导出任务（工厂方法）.
     * <p>
     * 创建一条新的导出任务记录，初始状态为DRAFT(0)。
     * </p>
     *
     * @param id           导出任务ID
     * @param taskName     任务名称
     * @param projectId    项目ID
     * @param stageId      阶段ID（可为null）
     * @param crfVersionId CRF版本ID（可为null）
     * @param fileFormat   导出文件格式
     * @return 新建的ExportTask实例
     */
    public static ExportTask create(ExportTaskId id,
                                     String taskName,
                                     ProjectId projectId,
                                     StageId stageId,
                                     CrfVersionId crfVersionId,
                                     FileFormat fileFormat) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(taskName, "taskName must not be null");
        Objects.requireNonNull(projectId, "projectId must not be null");
        Objects.requireNonNull(fileFormat, "fileFormat must not be null");

        if (taskName.trim().isEmpty()) {
            throw new IllegalArgumentException("taskName must not be empty");
        }

        ExportTask task = new ExportTask();
        task.id = id;
        task.taskName = taskName;
        task.projectId = projectId;
        task.stageId = stageId;
        task.crfVersionId = crfVersionId;
        task.status = ExportStatus.DRAFT;
        task.fileFormat = fileFormat;
        return task;
    }

    /**
     * 从持久化存储重建ExportTask（不含副作用，不注册事件）.
     *
     * @param id              导出任务ID
     * @param taskName        任务名称
     * @param projectId       项目ID
     * @param stageId         阶段ID
     * @param crfVersionId    CRF版本ID
     * @param status          状态
     * @param fileFormat      文件格式
     * @param auditUserId     审批用户ID
     * @param auditTime       审批时间
     * @param auditMessage    审批意见
     * @param fileUrl         文件URL
     * @param fileName        文件名
     * @param downloadCount   下载次数
     * @param failCount       失败次数
     * @param failMessage     失败信息
     * @param fieldConfigs    字段配置列表
     * @param filters         筛选条件列表
     * @param executionLogs   执行日志列表
     * @return 重建的ExportTask实例
     */
    public static ExportTask reconstruct(ExportTaskId id,
                                          String taskName,
                                          ProjectId projectId,
                                          StageId stageId,
                                          CrfVersionId crfVersionId,
                                          ExportStatus status,
                                          FileFormat fileFormat,
                                          String auditUserId,
                                          Date auditTime,
                                          String auditMessage,
                                          String fileUrl,
                                          String fileName,
                                          Integer downloadCount,
                                          Integer failCount,
                                          String failMessage,
                                          List<ExportFieldConfig> fieldConfigs,
                                          List<ExportFilter> filters,
                                          List<ExportExecutionLog> executionLogs) {
        ExportTask task = new ExportTask();
        task.id = id;
        task.taskName = taskName;
        task.projectId = projectId;
        task.stageId = stageId;
        task.crfVersionId = crfVersionId;
        task.status = status;
        task.fileFormat = fileFormat;
        task.auditUserId = auditUserId;
        task.auditTime = auditTime;
        task.auditMessage = auditMessage;
        task.fileUrl = fileUrl;
        task.fileName = fileName;
        task.downloadCount = downloadCount != null ? downloadCount : 0;
        task.failCount = failCount != null ? failCount : 0;
        task.failMessage = failMessage;
        task.fieldConfigs = fieldConfigs != null ? new ArrayList<>(fieldConfigs) : new ArrayList<ExportFieldConfig>();
        task.filters = filters != null ? new ArrayList<>(filters) : new ArrayList<ExportFilter>();
        task.executionLogs = executionLogs != null ? new ArrayList<>(executionLogs) : new ArrayList<ExportExecutionLog>();
        return task;
    }

    // ========== 业务方法（状态转换） ==========

    /**
     * 提交审批.
     * <p>
     * 状态转换：DRAFT(0) → PENDING_APPROVAL(1)。
     * 注册 {@link ExportTaskSubmittedEvent} 事件。
     * </p>
     *
     * @throws BusinessRuleViolationException 如果当前状态不是DRAFT
     */
    public void submit() {
        if (status != ExportStatus.DRAFT) {
            throw new BusinessRuleViolationException(
                    "EXPORT_INVALID_TRANSITION",
                    "Cannot submit export task " + id + ": current status is " + status
                            + ", expected DRAFT");
        }

        this.status = ExportStatus.PENDING_APPROVAL;

        registerEvent(new ExportTaskSubmittedEvent(id, taskName));
    }

    /**
     * 审批通过.
     * <p>
     * 状态转换：PENDING_APPROVAL(1) → APPROVED(2)。
     * 设置审批相关信息。注册 {@link ExportTaskApprovedEvent} 事件。
     * </p>
     *
     * @param userId  审批用户ID
     * @param message 审批意见
     * @throws BusinessRuleViolationException 如果当前状态不是PENDING_APPROVAL
     */
    public void approve(String userId, String message) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(message, "message must not be null");

        if (status != ExportStatus.PENDING_APPROVAL) {
            throw new BusinessRuleViolationException(
                    "EXPORT_INVALID_TRANSITION",
                    "Cannot approve export task " + id + ": current status is " + status
                            + ", expected PENDING_APPROVAL");
        }

        this.status = ExportStatus.APPROVED;
        this.auditUserId = userId;
        this.auditTime = new Date();
        this.auditMessage = message;

        registerEvent(new ExportTaskApprovedEvent(id, userId));
    }

    /**
     * 驳回审批.
     * <p>
     * 状态转换：PENDING_APPROVAL(1) → DRAFT(0)。
     * 设置审批相关信息。注册 {@link ExportTaskRejectedEvent} 事件。
     * </p>
     *
     * @param userId  审批用户ID
     * @param message 驳回原因
     * @throws BusinessRuleViolationException 如果当前状态不是PENDING_APPROVAL
     */
    public void reject(String userId, String message) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(message, "message must not be null");

        if (status != ExportStatus.PENDING_APPROVAL) {
            throw new BusinessRuleViolationException(
                    "EXPORT_INVALID_TRANSITION",
                    "Cannot reject export task " + id + ": current status is " + status
                            + ", expected PENDING_APPROVAL");
        }

        this.status = ExportStatus.DRAFT;
        this.auditUserId = userId;
        this.auditTime = new Date();
        this.auditMessage = message;

        registerEvent(new ExportTaskRejectedEvent(id, userId, message));
    }

    /**
     * 标记为导出中.
     * <p>
     * 状态转换：APPROVED(2) → EXPORTING(3) 或 FAILED(5) → EXPORTING(3)。
     * 注册 {@link ExportExecutionStartedEvent} 事件。
     * </p>
     *
     * @throws BusinessRuleViolationException 如果当前状态不是APPROVED或FAILED
     */
    public void markExporting() {
        if (status != ExportStatus.APPROVED && status != ExportStatus.FAILED) {
            throw new BusinessRuleViolationException(
                    "EXPORT_INVALID_TRANSITION",
                    "Cannot start export task " + id + ": current status is " + status
                            + ", expected APPROVED or FAILED");
        }

        this.status = ExportStatus.EXPORTING;

        registerEvent(new ExportExecutionStartedEvent(id));
    }

    /**
     * 标记为完成.
     * <p>
     * 状态转换：EXPORTING(3) → COMPLETED(4)。
     * 设置导出文件信息。注册 {@link ExportExecutionCompletedEvent} 事件。
     * </p>
     *
     * @param fileUrl  导出文件的访问URL
     * @throws BusinessRuleViolationException 如果当前状态不是EXPORTING
     */
    public void markCompleted(String fileUrl) {
        Objects.requireNonNull(fileUrl, "fileUrl must not be null");

        if (status != ExportStatus.EXPORTING) {
            throw new BusinessRuleViolationException(
                    "EXPORT_INVALID_TRANSITION",
                    "Cannot complete export task " + id + ": current status is " + status
                            + ", expected EXPORTING");
        }

        this.status = ExportStatus.COMPLETED;
        this.fileUrl = fileUrl;
        this.failMessage = null;

        registerEvent(new ExportExecutionCompletedEvent(id, fileUrl));
    }

    /**
     * 标记为失败.
     * <p>
     * 状态转换：EXPORTING(3) → FAILED(5)。
     * 增加失败计数。注册 {@link ExportExecutionFailedEvent} 事件。
     * </p>
     *
     * @param message 失败原因描述
     * @throws BusinessRuleViolationException 如果当前状态不是EXPORTING
     */
    public void markFailed(String message) {
        Objects.requireNonNull(message, "message must not be null");

        if (status != ExportStatus.EXPORTING) {
            throw new BusinessRuleViolationException(
                    "EXPORT_INVALID_TRANSITION",
                    "Cannot fail export task " + id + ": current status is " + status
                            + ", expected EXPORTING");
        }

        this.status = ExportStatus.FAILED;
        this.failCount = (this.failCount == null ? 0 : this.failCount) + 1;
        this.failMessage = message;

        registerEvent(new ExportExecutionFailedEvent(id, message, failCount));
    }

    // ========== 查询方法 ==========

    /**
     * 判断是否可以重试导出.
     *
     * @return true 如果状态为FAILED且失败次数未超过最大重试次数
     */
    public boolean canRetry() {
        return status == ExportStatus.FAILED && failCount < MAX_RETRY_COUNT;
    }

    /**
     * 判断是否可以修改（编辑）.
     *
     * @return true 如果状态为DRAFT
     */
    public boolean canModify() {
        return status == ExportStatus.DRAFT;
    }

    // ========== 字段和筛选条件管理 ==========

    /**
     * 添加导出字段配置.
     *
     * @param config 字段配置
     */
    public void addFieldConfig(ExportFieldConfig config) {
        Objects.requireNonNull(config, "config must not be null");
        if (!canModify()) {
            throw new BusinessRuleViolationException(
                    "EXPORT_CANNOT_MODIFY",
                    "Cannot modify field configs when task status is " + status);
        }
        this.fieldConfigs.add(config);
    }

    /**
     * 添加筛选条件.
     *
     * @param filter 筛选条件
     */
    public void addFilter(ExportFilter filter) {
        Objects.requireNonNull(filter, "filter must not be null");
        if (!canModify()) {
            throw new BusinessRuleViolationException(
                    "EXPORT_CANNOT_MODIFY",
                    "Cannot modify filters when task status is " + status);
        }
        this.filters.add(filter);
    }

    // ========== Getter方法 ==========

    @Override
    public ExportTaskId getId() {
        return id;
    }

    /**
     * 获取任务名称.
     *
     * @return 任务名称
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 获取项目ID.
     *
     * @return ProjectId
     */
    public ProjectId getProjectId() {
        return projectId;
    }

    /**
     * 获取阶段ID.
     *
     * @return StageId，可能为null
     */
    public StageId getStageId() {
        return stageId;
    }

    /**
     * 获取CRF版本ID.
     *
     * @return CrfVersionId，可能为null
     */
    public CrfVersionId getCrfVersionId() {
        return crfVersionId;
    }

    /**
     * 获取导出任务状态.
     *
     * @return ExportStatus
     */
    public ExportStatus getStatus() {
        return status;
    }

    /**
     * 获取导出文件格式.
     *
     * @return FileFormat
     */
    public FileFormat getFileFormat() {
        return fileFormat;
    }

    /**
     * 获取审批用户ID.
     *
     * @return 审批用户ID
     */
    public String getAuditUserId() {
        return auditUserId;
    }

    /**
     * 获取审批时间.
     *
     * @return Date
     */
    public Date getAuditTime() {
        return auditTime != null ? (Date) auditTime.clone() : null;
    }

    /**
     * 获取审批意见.
     *
     * @return 审批意见
     */
    public String getAuditMessage() {
        return auditMessage;
    }

    /**
     * 获取导出文件URL.
     *
     * @return 文件URL
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 获取导出文件名.
     *
     * @return 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 获取下载次数.
     *
     * @return 下载次数
     */
    public Integer getDownloadCount() {
        return downloadCount;
    }

    /**
     * 获取失败次数.
     *
     * @return 失败次数
     */
    public Integer getFailCount() {
        return failCount;
    }

    /**
     * 获取失败信息.
     *
     * @return 失败信息
     */
    public String getFailMessage() {
        return failMessage;
    }

    /**
     * 获取字段配置列表（不可修改）.
     *
     * @return 字段配置列表
     */
    public List<ExportFieldConfig> getFieldConfigs() {
        return Collections.unmodifiableList(fieldConfigs);
    }

    /**
     * 获取筛选条件列表（不可修改）.
     *
     * @return 筛选条件列表
     */
    public List<ExportFilter> getFilters() {
        return Collections.unmodifiableList(filters);
    }

    /**
     * 获取执行日志列表（不可修改）.
     *
     * @return 执行日志列表
     */
    public List<ExportExecutionLog> getExecutionLogs() {
        return Collections.unmodifiableList(executionLogs);
    }

    @Override
    public String toString() {
        return "ExportTask{"
                + "id=" + id
                + ", taskName='" + taskName + '\''
                + ", status=" + status
                + ", fileFormat=" + fileFormat
                + '}';
    }
}
