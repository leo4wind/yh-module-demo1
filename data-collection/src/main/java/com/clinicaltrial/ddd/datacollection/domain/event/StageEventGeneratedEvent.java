package com.clinicaltrial.ddd.datacollection.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * StageEventGeneratedEvent — 受试者阶段生成事件.
 * <p>
 * 当受试者入组后，系统自动为每个自动添加的阶段生成SubjectStage聚合时触发。
 * 表示一个数据采集阶段已为特定受试者创建。
 * </p>
 */
public class StageEventGeneratedEvent implements DomainEvent {

    private final SubjectStageId subjectStageId;
    private final SubjectId subjectId;
    private final StageId stageId;
    private final VisitPlanId planEventId;
    private final LocalDateTime occurredOn;

    /**
     * 构造StageEventGeneratedEvent.
     *
     * @param subjectStageId 生成的受试者阶段ID
     * @param subjectId      受试者ID
     * @param stageId        试验阶段ID
     * @param planEventId    触发的访视计划ID
     */
    public StageEventGeneratedEvent(SubjectStageId subjectStageId, SubjectId subjectId,
                                    StageId stageId, VisitPlanId planEventId) {
        this.subjectStageId = subjectStageId;
        this.subjectId = subjectId;
        this.stageId = stageId;
        this.planEventId = planEventId;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取生成的受试者阶段ID.
     *
     * @return SubjectStageId
     */
    public SubjectStageId getSubjectStageId() {
        return subjectStageId;
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
     * 获取试验阶段ID.
     *
     * @return StageId
     */
    public StageId getStageId() {
        return stageId;
    }

    /**
     * 获取触发的访视计划ID.
     *
     * @return VisitPlanId
     */
    public VisitPlanId getPlanEventId() {
        return planEventId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StageEventGeneratedEvent that = (StageEventGeneratedEvent) o;
        return Objects.equals(subjectStageId, that.subjectStageId)
                && Objects.equals(subjectId, that.subjectId)
                && Objects.equals(stageId, that.stageId)
                && Objects.equals(planEventId, that.planEventId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectStageId, subjectId, stageId, planEventId, occurredOn);
    }

    @Override
    public String toString() {
        return "StageEventGeneratedEvent{"
                + "subjectStageId=" + subjectStageId
                + ", subjectId=" + subjectId
                + ", stageId=" + stageId
                + ", planEventId=" + planEventId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
