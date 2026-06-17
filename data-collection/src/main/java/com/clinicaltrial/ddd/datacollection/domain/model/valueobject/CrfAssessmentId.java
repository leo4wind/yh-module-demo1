package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * CrfAssessmentId — CRF评估记录标识值对象.
 * <p>
 * 包装Long类型，作为CrfAssessment聚合的唯一标识。
 * </p>
 */
public class CrfAssessmentId implements ValueObject {

    private final Long value;

    /**
     * 构造CrfAssessmentId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public CrfAssessmentId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CrfAssessmentId value must not be null");
        }
        this.value = value;
    }

    /**
     * 获取原始Long值.
     *
     * @return 原始Long值
     */
    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfAssessmentId that = (CrfAssessmentId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CrfAssessmentId{" + "value=" + value + '}';
    }
}
