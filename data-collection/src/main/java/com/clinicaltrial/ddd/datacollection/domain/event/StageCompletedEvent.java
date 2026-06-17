package com.clinicaltrial.ddd.datacollection.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * StageCompletedEvent — 受试者阶段完成事件.
 * <p>
 * 当SubjectStage内的所有CRF评估均完成后触发。
 * 表示受试者在某个试验阶段的数据采集工作已完成。
 * </p>
 */
public class StageCompletedEvent implements DomainEvent {

    private final SubjectStageId subjectStageId;
    private final SubjectId subjectId;
    private final StageId stageId;
    private final LocalDateTime occurredOn;

    /**
     * 构造StageCompletedEvent.
     *
     * @param subjectStageId 完成的受试者阶段ID
     * @param subjectId      受试者ID
     * @param stageId        试验阶段ID
     */
    public StageCompletedEvent(SubjectStageId subjectStageId, SubjectId subjectId, StageId stageId) {
        this.subjectStageId = subjectStageId;
        this.subjectId = subjectId;
        this.stageId = stageId;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取完成的受试者阶段ID.
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

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "StageCompleted: stage " + subjectStageId
                + " for subject " + subjectId
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
        StageCompletedEvent that = (StageCompletedEvent) o;
        return Objects.equals(subjectStageId, that.subjectStageId)
                && Objects.equals(subjectId, that.subjectId)
                && Objects.equals(stageId, that.stageId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectStageId, subjectId, stageId, occurredOn);
    }

    @Override
    public String toString() {
        return "StageCompletedEvent{"
                + "subjectStageId=" + subjectStageId
                + ", subjectId=" + subjectId
                + ", stageId=" + stageId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
