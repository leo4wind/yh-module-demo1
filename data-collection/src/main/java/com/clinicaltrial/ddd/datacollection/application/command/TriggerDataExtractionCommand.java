package com.clinicaltrial.ddd.datacollection.application.command;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.io.Serializable;
import java.util.Objects;

/**
 * TriggerDataExtractionCommand — 触发数据抽取命令.
 * <p>
 * 封装触发外部系统（HIS/LIS）数据抽取的输入参数。
 * 用于从HIS/LIS等外部系统自动拉取受试者的临床数据并填充到CRF评估中。
 * </p>
 */
public class TriggerDataExtractionCommand implements Serializable {

    private final SubjectId subjectsUserId;
    private final StageId stageId;
    private final Long userId;

    /**
     * 构造TriggerDataExtractionCommand.
     *
     * @param subjectsUserId 受试者ID
     * @param stageId        试验阶段ID
     * @param userId         操作用户ID
     */
    public TriggerDataExtractionCommand(SubjectId subjectsUserId, StageId stageId, Long userId) {
        this.subjectsUserId = subjectsUserId;
        this.stageId = stageId;
        this.userId = userId;
    }

    /**
     * 获取受试者ID.
     *
     * @return SubjectId
     */
    public SubjectId getSubjectsUserId() {
        return subjectsUserId;
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
     * 获取操作用户ID.
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TriggerDataExtractionCommand that = (TriggerDataExtractionCommand) o;
        return Objects.equals(subjectsUserId, that.subjectsUserId)
                && Objects.equals(stageId, that.stageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectsUserId, stageId);
    }

    @Override
    public String toString() {
        return "TriggerDataExtractionCommand{"
                + "subjectsUserId=" + subjectsUserId
                + ", stageId=" + stageId
                + '}';
    }
}
