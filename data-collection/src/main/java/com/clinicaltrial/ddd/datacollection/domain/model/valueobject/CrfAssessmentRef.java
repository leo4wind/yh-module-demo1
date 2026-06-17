package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * CrfAssessmentRef — CRF评估引用值对象.
 * <p>
 * 在SubjectStage聚合中用于引用CrfAssessment聚合ID。
 * 这是对另一个聚合的弱引用（仅ID），不包含完整实体。
 * </p>
 */
public class CrfAssessmentRef implements ValueObject {

    private final CrfAssessmentId assessmentId;

    /**
     * 构造CrfAssessmentRef.
     *
     * @param assessmentId CRF评估ID，不能为空
     * @throws IllegalArgumentException 如果assessmentId为null
     */
    public CrfAssessmentRef(CrfAssessmentId assessmentId) {
        if (assessmentId == null) {
            throw new IllegalArgumentException("AssessmentId must not be null");
        }
        this.assessmentId = assessmentId;
    }

    /**
     * 获取被引用的CRF评估ID.
     *
     * @return CRF评估ID
     */
    public CrfAssessmentId getAssessmentId() {
        return assessmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfAssessmentRef that = (CrfAssessmentRef) o;
        return Objects.equals(assessmentId, that.assessmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId);
    }

    @Override
    public String toString() {
        return "CrfAssessmentRef{" + "assessmentId=" + assessmentId + '}';
    }
}
