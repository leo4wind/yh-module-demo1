package com.clinicaltrial.ddd.datacollection.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * CrfFieldValueChangedEvent — CRF字段值变更事件.
 * <p>
 * 当CRF评估中的某个字段值被新增或修改时触发。
 * 用于审计追踪、数据同步及触发后续业务流程。
 * </p>
 */
public class CrfFieldValueChangedEvent implements DomainEvent {

    private final CrfAssessmentId assessmentId;
    private final String fieldCode;
    private final String oldValue;
    private final String newValue;
    private final Long userId;
    private final LocalDateTime occurredOn;

    /**
     * 构造CrfFieldValueChangedEvent（新增字段值，oldValue为null）.
     *
     * @param assessmentId CRF评估ID
     * @param fieldCode    字段编码
     * @param newValue     新值
     * @param userId       操作用户ID
     */
    public CrfFieldValueChangedEvent(CrfAssessmentId assessmentId, String fieldCode,
                                     String newValue, Long userId) {
        this(assessmentId, fieldCode, null, newValue, userId);
    }

    /**
     * 构造CrfFieldValueChangedEvent（修改字段值）.
     *
     * @param assessmentId CRF评估ID
     * @param fieldCode    字段编码
     * @param oldValue     旧值
     * @param newValue     新值
     * @param userId       操作用户ID
     */
    public CrfFieldValueChangedEvent(CrfAssessmentId assessmentId, String fieldCode,
                                     String oldValue, String newValue, Long userId) {
        this.assessmentId = assessmentId;
        this.fieldCode = fieldCode;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.userId = userId;
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
     * 获取变更的字段编码.
     *
     * @return 字段编码
     */
    public String getFieldCode() {
        return fieldCode;
    }

    /**
     * 获取旧值.
     *
     * @return 旧值，新增时为null
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * 获取新值.
     *
     * @return 新值
     */
    public String getNewValue() {
        return newValue;
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
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        if (oldValue == null) {
            return "CrfFieldValueAdded: assessment " + assessmentId
                    + " field " + fieldCode
                    + " = " + newValue;
        }
        return "CrfFieldValueChanged: assessment " + assessmentId
                + " field " + fieldCode
                + " from " + oldValue + " to " + newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfFieldValueChangedEvent that = (CrfFieldValueChangedEvent) o;
        return Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(fieldCode, that.fieldCode)
                && Objects.equals(oldValue, that.oldValue)
                && Objects.equals(newValue, that.newValue)
                && Objects.equals(userId, that.userId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assessmentId, fieldCode, oldValue, newValue, userId, occurredOn);
    }

    @Override
    public String toString() {
        return "CrfFieldValueChangedEvent{"
                + "assessmentId=" + assessmentId
                + ", fieldCode='" + fieldCode + '\''
                + ", oldValue='" + oldValue + '\''
                + ", newValue='" + newValue + '\''
                + ", userId=" + userId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
