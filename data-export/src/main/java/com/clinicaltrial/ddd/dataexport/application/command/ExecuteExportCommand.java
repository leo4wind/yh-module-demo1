package com.clinicaltrial.ddd.dataexport.application.command;

import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.Objects;

/**
 * ExecuteExportCommand — 执行导出指令.
 * <p>
 * 封装执行数据导出所需的输入参数。
 * 触发导出任务的实际执行，将已批准(APPROVED)或失败(FAILED)状态
 * 的导出任务转为导出中(EXPORTING)并执行导出。
 * </p>
 */
public class ExecuteExportCommand {

    private final ExportTaskId taskId;

    /**
     * 构造ExecuteExportCommand.
     *
     * @param taskId 导出任务ID
     */
    public ExecuteExportCommand(ExportTaskId taskId) {
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
