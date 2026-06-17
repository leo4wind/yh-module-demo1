package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * SubjectStageId — 受试者阶段标识值对象.
 * <p>
 * 包装Long类型，作为SubjectStage聚合的唯一标识。
 * </p>
 */
public class SubjectStageId implements ValueObject {

    private final Long value;

    /**
     * 构造SubjectStageId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public SubjectStageId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("SubjectStageId value must not be null");
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
        SubjectStageId that = (SubjectStageId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SubjectStageId{" + "value=" + value + '}';
    }
}
