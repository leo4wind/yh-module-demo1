package com.clinicaltrial.ddd.dataexport.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ExportExecutionFailedEvent — 导出执行失败事件.
 * <p>
 * 当导出任务执行失败（转换为FAILED状态）时触发。
 * 包含失败原因和当前失败次数（用于判断是否允许重试）。
 * </p>
 */
public class ExportExecutionFailedEvent implements DomainEvent {

    private final ExportTaskId taskId;
    private final String failMessage;
    private final int failCount;
    private final LocalDateTime occurredOn;

    /**
     * 构造ExportExecutionFailedEvent.
     *
     * @param taskId      导出任务ID
     * @param failMessage 失败原因描述
     * @param failCount   当前失败次数
     */
    public ExportExecutionFailedEvent(ExportTaskId taskId, String failMessage, int failCount) {
        this.taskId = taskId;
        this.failMessage = failMessage;
        this.failCount = failCount;
        this.occurredOn = LocalDateTime.now();
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
     * 获取失败原因描述.
     *
     * @return 失败原因
     */
    public String getFailMessage() {
        return failMessage;
    }

    /**
     * 获取当前失败次数.
     *
     * @return 失败次数
     */
    public int getFailCount() {
        return failCount;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ExportExecutionFailed: task " + taskId + ", failCount=" + failCount
                + ", message=" + failMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportExecutionFailedEvent that = (ExportExecutionFailedEvent) o;
        return failCount == that.failCount
                && Objects.equals(taskId, that.taskId)
                && Objects.equals(failMessage, that.failMessage)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, failMessage, failCount, occurredOn);
    }

    @Override
    public String toString() {
        return "ExportExecutionFailedEvent{"
                + "taskId=" + taskId
                + ", failMessage='" + failMessage + '\''
                + ", failCount=" + failCount
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
