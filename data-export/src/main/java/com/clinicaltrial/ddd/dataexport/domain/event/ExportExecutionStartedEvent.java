package com.clinicaltrial.ddd.dataexport.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ExportExecutionStartedEvent — 导出执行开始事件.
 * <p>
 * 当导出任务开始执行（转换为EXPORTING状态）时触发。
 * 表示系统开始处理实际的导出操作。
 * </p>
 */
public class ExportExecutionStartedEvent implements DomainEvent {

    private final ExportTaskId taskId;
    private final LocalDateTime occurredOn;

    /**
     * 构造ExportExecutionStartedEvent.
     *
     * @param taskId 导出任务ID
     */
    public ExportExecutionStartedEvent(ExportTaskId taskId) {
        this.taskId = taskId;
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

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ExportExecutionStarted: task " + taskId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportExecutionStartedEvent that = (ExportExecutionStartedEvent) o;
        return Objects.equals(taskId, that.taskId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, occurredOn);
    }

    @Override
    public String toString() {
        return "ExportExecutionStartedEvent{"
                + "taskId=" + taskId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
