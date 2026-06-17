package com.clinicaltrial.ddd.dataexport.application.command;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.Objects;

/**
 * RejectExportTaskCommand — 驳回导出任务指令.
 * <p>
 * 封装驳回导出任务所需的输入参数。
 * 将待审批(PENDING_APPROVAL)状态的导出任务驳回回草稿(DRAFT)状态。
 * </p>
 */
public class RejectExportTaskCommand {

    private final ExportTaskId taskId;
    private final String userId;
    private final String reason;

    /**
     * 构造RejectExportTaskCommand.
     *
     * @param taskId 导出任务ID
     * @param userId 审批用户ID
     * @param reason 驳回原因
     */
    public RejectExportTaskCommand(ExportTaskId taskId, String userId, String reason) {
        this.taskId = Objects.requireNonNull(taskId, "taskId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.reason = Objects.requireNonNull(reason, "reason must not be null");
    }

    /**
     * 获取导出任务ID.
     *
     * @return ExportTaskId
     */
    public ExportTaskId getTaskId() {
        return taskId;
    }

    /**
     * 获取审批用户ID.
     *
     * @return 用户ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取驳回原因.
     *
     * @return 驳回原因
     */
    public String getReason() {
        return reason;
    }
}
