package com.clinicaltrial.ddd.dataexport.application.command;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.Objects;

/**
 * ApproveExportTaskCommand — 审批通过导出任务指令.
 * <p>
 * 封装审批通过导出任务所需的输入参数。
 * 将待审批(PENDING_APPROVAL)状态的导出任务变更为已批准(APPROVED)状态。
 * </p>
 */
public class ApproveExportTaskCommand {

    private final ExportTaskId taskId;
    private final String userId;
    private final String message;

    /**
     * 构造ApproveExportTaskCommand.
     *
     * @param taskId  导出任务ID
     * @param userId  审批用户ID
     * @param message 审批意见
     */
    public ApproveExportTaskCommand(ExportTaskId taskId, String userId, String message) {
        this.taskId = Objects.requireNonNull(taskId, "taskId must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
        this.message = Objects.requireNonNull(message, "message must not be null");
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
     * 获取审批意见.
     *
     * @return 审批意见
     */
    public String getMessage() {
        return message;
    }
}
