package com.clinicaltrial.ddd.datacollection.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * FollowUpTriggeredEvent — 随访触发事件.
 * <p>
 * 当某个阶段完成后，如果配置了自动触发下一个访视/随访计划，则触发此事件。
 * 该事件将启动下一个阶段的SubjectStage生成流程。
 * </p>
 */
public class FollowUpTriggeredEvent implements DomainEvent {

    private final SubjectId subjectId;
    private final VisitPlanId planEventId;
    private final StageId targetStageId;
    private final SubjectStageId sourceSubjectStageId;
    private final LocalDateTime occurredOn;

    /**
     * 构造FollowUpTriggeredEvent.
     *
     * @param subjectId           受试者ID
     * @param planEventId         触发的访视计划ID
     * @param targetStageId       目标试验阶段ID
     * @param sourceSubjectStageId 来源受试者阶段ID
     */
    public FollowUpTriggeredEvent(SubjectId subjectId, VisitPlanId planEventId,
                                  StageId targetStageId, SubjectStageId sourceSubjectStageId) {
        this.subjectId = subjectId;
        this.planEventId = planEventId;
        this.targetStageId = targetStageId;
        this.sourceSubjectStageId = sourceSubjectStageId;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取受试者ID.
     *
     * @return SubjectId
     */
    public SubjectId getSubjectId() {
        return subjectId;
    }

    /**
     * 获取触发的访视计划ID.
     *
     * @return VisitPlanId
     */
    public VisitPlanId getPlanEventId() {
        return planEventId;
    }

    /**
     * 获取目标试验阶段ID.
     *
     * @return StageId
     */
    public StageId getTargetStageId() {
        return targetStageId;
    }

    /**
     * 获取来源受试者阶段ID.
     *
     * @return SubjectStageId
     */
    public SubjectStageId getSourceSubjectStageId() {
        return sourceSubjectStageId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "FollowUpTriggered: subject " + subjectId
                + " from stage " + sourceSubjectStageId
                + " to stage " + targetStageId
                + " via plan " + planEventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FollowUpTriggeredEvent that = (FollowUpTriggeredEvent) o;
        return Objects.equals(subjectId, that.subjectId)
                && Objects.equals(planEventId, that.planEventId)
                && Objects.equals(targetStageId, that.targetStageId)
                && Objects.equals(sourceSubjectStageId, that.sourceSubjectStageId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, planEventId, targetStageId, sourceSubjectStageId, occurredOn);
    }

    @Override
    public String toString() {
        return "FollowUpTriggeredEvent{"
                + "subjectId=" + subjectId
                + ", planEventId=" + planEventId
                + ", targetStageId=" + targetStageId
                + ", sourceSubjectStageId=" + sourceSubjectStageId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
