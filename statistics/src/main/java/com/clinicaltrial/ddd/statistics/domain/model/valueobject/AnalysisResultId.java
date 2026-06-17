package com.clinicaltrial.ddd.statistics.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * AnalysisResultId — 分析结果标识值对象.
 * <p>
 * 包装Long类型，作为AnalysisResult实体的唯一标识。
 * </p>
 */
public class AnalysisResultId implements ValueObject {

    private final Long value;

    /**
     * 构造AnalysisResultId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public AnalysisResultId(Long value) {
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
        AnalysisResultId that = (AnalysisResultId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AnalysisResultId{" + "value=" + value + '}';
    }
}
