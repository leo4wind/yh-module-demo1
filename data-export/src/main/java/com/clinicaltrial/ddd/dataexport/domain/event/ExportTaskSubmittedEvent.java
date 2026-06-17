package com.clinicaltrial.ddd.dataexport.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ExportTaskSubmittedEvent — 导出任务提交事件.
 * <p>
 * 当导出任务从草稿(DRAFT)提交为待审批(PENDING_APPROVAL)状态时触发。
 * </p>
 */
public class ExportTaskSubmittedEvent implements DomainEvent {

    private final ExportTaskId taskId;
    private final String taskName;
    private final LocalDateTime occurredOn;

    /**
     * 构造ExportTaskSubmittedEvent.
     *
     * @param taskId   导出任务ID
     * @param taskName 任务名称
     */
    public ExportTaskSubmittedEvent(ExportTaskId taskId, String taskName) {
        this.taskId = taskId;
        this.taskName = taskName;
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
     * 获取任务名称.
     *
     * @return 任务名称
     */
    public String getTaskName() {
        return taskName;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ExportTaskSubmitted: task " + taskId + " (" + taskName + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportTaskSubmittedEvent that = (ExportTaskSubmittedEvent) o;
        return Objects.equals(taskId, that.taskId)
                && Objects.equals(taskName, that.taskName)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, taskName, occurredOn);
    }

    @Override
    public String toString() {
        return "ExportTaskSubmittedEvent{"
                + "taskId=" + taskId
                + ", taskName='" + taskName + '\''
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
