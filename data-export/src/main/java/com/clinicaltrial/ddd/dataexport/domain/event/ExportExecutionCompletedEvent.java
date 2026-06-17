package com.clinicaltrial.ddd.dataexport.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ExportExecutionCompletedEvent — 导出执行完成事件.
 * <p>
 * 当导出任务执行完成（转换为COMPLETED状态）时触发。
 * 包含生成文件的访问URL。
 * </p>
 */
public class ExportExecutionCompletedEvent implements DomainEvent {

    private final ExportTaskId taskId;
    private final String fileUrl;
    private final LocalDateTime occurredOn;

    /**
     * 构造ExportExecutionCompletedEvent.
     *
     * @param taskId  导出任务ID
     * @param fileUrl 生成文件的访问URL
     */
    public ExportExecutionCompletedEvent(ExportTaskId taskId, String fileUrl) {
        this.taskId = taskId;
        this.fileUrl = fileUrl;
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
     * 获取文件访问URL.
     *
     * @return 文件URL
     */
    public String getFileUrl() {
        return fileUrl;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ExportExecutionCompleted: task " + taskId + ", fileUrl=" + fileUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportExecutionCompletedEvent that = (ExportExecutionCompletedEvent) o;
        return Objects.equals(taskId, that.taskId)
                && Objects.equals(fileUrl, that.fileUrl)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, fileUrl, occurredOn);
    }

    @Override
    public String toString() {
        return "ExportExecutionCompletedEvent{"
                + "taskId=" + taskId
                + ", fileUrl='" + fileUrl + '\''
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
