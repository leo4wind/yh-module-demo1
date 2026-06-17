package com.clinicaltrial.ddd.dataexport.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ExportTaskRejectedEvent — 导出任务审批驳回事件.
 * <p>
 * 当导出任务从待审批(PENDING_APPROVAL)被驳回回草稿(DRAFT)状态时触发。
 * 包含驳回原因说明。
 * </p>
 */
public class ExportTaskRejectedEvent implements DomainEvent {

    private final ExportTaskId taskId;
    private final String auditUserId;
    private final String reason;
    private final LocalDateTime occurredOn;

    /**
     * 构造ExportTaskRejectedEvent.
     *
     * @param taskId      导出任务ID
     * @param auditUserId 审批用户ID
     * @param reason      驳回原因
     */
    public ExportTaskRejectedEvent(ExportTaskId taskId, String auditUserId, String reason) {
        this.taskId = taskId;
        this.auditUserId = auditUserId;
        this.reason = reason;
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
     * 获取审批用户ID.
     *
     * @return 审批用户ID
     */
    public String getAuditUserId() {
        return auditUserId;
    }

    /**
     * 获取驳回原因.
     *
     * @return 驳回原因
     */
    public String getReason() {
        return reason;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ExportTaskRejected: task " + taskId + " rejected by " + auditUserId
                + ", reason: " + reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportTaskRejectedEvent that = (ExportTaskRejectedEvent) o;
        return Objects.equals(taskId, that.taskId)
                && Objects.equals(auditUserId, that.auditUserId)
                && Objects.equals(reason, that.reason)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, auditUserId, reason, occurredOn);
    }

    @Override
    public String toString() {
        return "ExportTaskRejectedEvent{"
                + "taskId=" + taskId
                + ", auditUserId='" + auditUserId + '\''
                + ", reason='" + reason + '\''
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
