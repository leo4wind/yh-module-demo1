package com.clinicaltrial.ddd.dataexport.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * ExportTaskApprovedEvent — 导出任务审批通过事件.
 * <p>
 * 当导出任务从待审批(PENDING_APPROVAL)变更为已批准(APPROVED)状态时触发。
 * 表示审批人同意执行该导出任务。
 * </p>
 */
public class ExportTaskApprovedEvent implements DomainEvent {

    private final ExportTaskId taskId;
    private final String auditUserId;
    private final LocalDateTime occurredOn;

    /**
     * 构造ExportTaskApprovedEvent.
     *
     * @param taskId      导出任务ID
     * @param auditUserId 审批用户ID
     */
    public ExportTaskApprovedEvent(ExportTaskId taskId, String auditUserId) {
        this.taskId = taskId;
        this.auditUserId = auditUserId;
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

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ExportTaskApproved: task " + taskId + " approved by " + auditUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExportTaskApprovedEvent that = (ExportTaskApprovedEvent) o;
        return Objects.equals(taskId, that.taskId)
                && Objects.equals(auditUserId, that.auditUserId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, auditUserId, occurredOn);
    }

    @Override
    public String toString() {
        return "ExportTaskApprovedEvent{"
                + "taskId=" + taskId
                + ", auditUserId='" + auditUserId + '\''
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
