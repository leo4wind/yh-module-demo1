package com.clinicaltrial.ddd.datacollection.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CrfCompletenessChangedEvent — CRF评估完整性变更事件.
 * <p>
 * 当CRF评估的填写完整性发生变化时触发。
 * 完整性变化可能由字段值的新增或修改引起。
 * </p>
 */
public class CrfCompletenessChangedEvent implements DomainEvent {

    private final CrfAssessmentId assessmentId;
    private final Completeness oldCompleteness;
    private final Completeness newCompleteness;
    private final LocalDateTime occurredOn;

    /**
     * 构造CrfCompletenessChangedEvent.
     *
     * @param assessmentId     CRF评估ID
     * @param oldCompleteness  变更前的完整性
     * @param newCompleteness  变更后的完整性
     */
    public CrfCompletenessChangedEvent(CrfAssessmentId assessmentId,
                                       Completeness oldCompleteness,
                                       Completeness newCompleteness) {
        this.assessmentId = assessmentId;
        this.oldCompleteness = oldCompleteness;
        this.newCompleteness = newCompleteness;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取CRF评估ID.
     *
     * @return CrfAssessmentId
     */
    public CrfAssessmentId getAssessmentId() {
        return assessmentId;
    }

    /**
     * 获取变更前的完整性.
     *
     * @return 旧Completeness
     */
    public Completeness getOldCompleteness() {
        return oldCompleteness;
    }

    /**
     * 获取变更后的完整性.
     *
     * @return 新Completeness
     */
    public Completeness getNewCompleteness() {
        return newCompleteness;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "CrfCompletenessChanged: assessment " + assessmentId
                + " changed from " + oldCompleteness
                + " to " + newCompleteness
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
        CrfCompletenessChangedEvent that = (CrfCompletenessChangedEvent) o;
        return Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(oldCompleteness, that.oldCompleteness)
                && Objects.equals(newCompleteness, that.newCompleteness)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId, oldCompleteness, newCompleteness, occurredOn);
    }

    @Override
    public String toString() {
        return "CrfCompletenessChangedEvent{"
                + "assessmentId=" + assessmentId
                + ", oldCompleteness=" + oldCompleteness
                + ", newCompleteness=" + newCompleteness
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
