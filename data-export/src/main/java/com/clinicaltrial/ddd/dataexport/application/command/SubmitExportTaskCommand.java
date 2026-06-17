package com.clinicaltrial.ddd.dataexport.application.command;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.Objects;

/**
 * SubmitExportTaskCommand — 提交导出任务审批指令.
 * <p>
 * 封装提交导出任务审批所需的输入参数。
 * 将草稿(DRAFT)状态的导出任务提交为待审批(PENDING_APPROVAL)状态。
 * </p>
 */
public class SubmitExportTaskCommand {

    private final ExportTaskId taskId;

    /**
     * 构造SubmitExportTaskCommand.
     *
     * @param taskId 导出任务ID
     */
    public SubmitExportTaskCommand(ExportTaskId taskId) {
        this.taskId = Objects.requireNonNull(taskId, "taskId must not be null");
    }

    /**
     * 获取导出任务ID.
     *
     * @return ExportTaskId
     */
    public ExportTaskId getTaskId() {
        return taskId;
    }
}
