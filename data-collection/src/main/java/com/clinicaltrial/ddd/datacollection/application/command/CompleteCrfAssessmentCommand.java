package com.clinicaltrial.ddd.datacollection.application.command;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;

import java.io.Serializable;
import java.util.Objects;

/**
 * CompleteCrfAssessmentCommand — 完成CRF评估命令.
 * <p>
 * 封装手动完成CRF评估的输入参数。
 * 当CRF评估的完整性达到100%时，调用方可以通过此命令触发完成确认流程。
 * </p>
 */
public class CompleteCrfAssessmentCommand implements Serializable {

    private final CrfAssessmentId assessmentId;
    private final Long userId;

    /**
     * 构造CompleteCrfAssessmentCommand.
     *
     * @param assessmentId CRF评估ID
     * @param userId       操作用户ID
     */
    public CompleteCrfAssessmentCommand(CrfAssessmentId assessmentId, Long userId) {
        this.assessmentId = assessmentId;
        this.userId = userId;
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
        CompleteCrfAssessmentCommand that = (CompleteCrfAssessmentCommand) o;
        return Objects.equals(assessmentId, that.assessmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId);
    }

    @Override
    public String toString() {
        return "CompleteCrfAssessmentCommand{"
                + "assessmentId=" + assessmentId
                + '}';
    }
}
