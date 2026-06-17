package com.clinicaltrial.ddd.datacollection.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CrfCompletedEvent — CRF评估完成事件.
 * <p>
 * 当CRF评估的完整性达到100%且状态自动转换为COMPLETED时触发。
 * 该事件会触发：
 * <ul>
 *   <li>自动评分（AutoScoringService监听）</li>
 *   <li>阶段完成检查（CrfFillingApplicationService检查所属阶段是否可完成）</li>
 * </ul>
 * </p>
 */
public class CrfCompletedEvent implements DomainEvent {

    private final CrfAssessmentId assessmentId;
    private final SubjectStageId subjectStageId;
    private final LocalDateTime occurredOn;

    /**
     * 构造CrfCompletedEvent.
     *
     * @param assessmentId   完成的CRF评估ID
     * @param subjectStageId 所属的受试者阶段ID
     */
    public CrfCompletedEvent(CrfAssessmentId assessmentId, SubjectStageId subjectStageId) {
        this.assessmentId = assessmentId;
        this.subjectStageId = subjectStageId;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取完成的CRF评估ID.
     *
     * @return CrfAssessmentId
     */
    public CrfAssessmentId getAssessmentId() {
        return assessmentId;
    }

    /**
     * 获取所属的受试者阶段ID.
     *
     * @return SubjectStageId
     */
    public SubjectStageId getSubjectStageId() {
        return subjectStageId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "CrfCompleted: assessment " + assessmentId
                + " in stage " + subjectStageId
                + " completed at " + occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfCompletedEvent that = (CrfCompletedEvent) o;
        return Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(subjectStageId, that.subjectStageId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId, subjectStageId, occurredOn);
    }

    @Override
    public String toString() {
        return "CrfCompletedEvent{"
                + "assessmentId=" + assessmentId
                + ", subjectStageId=" + subjectStageId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
