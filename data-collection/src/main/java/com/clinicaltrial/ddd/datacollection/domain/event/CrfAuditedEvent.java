package com.clinicaltrial.ddd.datacollection.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CrfAuditedEvent — CRF评估审核通过事件.
 * <p>
 * 当监查员对CRF评估执行审核操作并通过时触发。
 * 通常在COMPLETED或QUERIED状态下执行审核后产生。
 * </p>
 */
public class CrfAuditedEvent implements DomainEvent {

    private final CrfAssessmentId assessmentId;
    private final Long auditUserId;
    private final LocalDateTime occurredOn;

    /**
     * 构造CrfAuditedEvent.
     *
     * @param assessmentId 被审核的CRF评估ID
     * @param auditUserId  执行审核的用户ID
     */
    public CrfAuditedEvent(CrfAssessmentId assessmentId, Long auditUserId) {
        this.assessmentId = assessmentId;
        this.auditUserId = auditUserId;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取被审核的CRF评估ID.
     *
     * @return CrfAssessmentId
     */
    public CrfAssessmentId getAssessmentId() {
        return assessmentId;
    }

    /**
     * 获取执行审核的用户ID.
     *
     * @return 用户ID
     */
    public Long getAuditUserId() {
        return auditUserId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "CrfAudited: assessment " + assessmentId
                + " audited by user " + auditUserId
                + " at " + occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfAuditedEvent that = (CrfAuditedEvent) o;
        return Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(auditUserId, that.auditUserId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId, auditUserId, occurredOn);
    }

    @Override
    public String toString() {
        return "CrfAuditedEvent{"
                + "assessmentId=" + assessmentId
                + ", auditUserId=" + auditUserId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
