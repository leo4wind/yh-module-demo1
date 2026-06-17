package com.clinicaltrial.ddd.statistics.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * AnalysisProjectId — 分析项目标识值对象.
 * <p>
 * 包装Long类型，作为AnalysisProject聚合的唯一标识。
 * 用于在统计分析上下文中唯一定位一个分析项目。
 * </p>
 */
public class AnalysisProjectId implements ValueObject {

    private final Long value;

    /**
     * 构造AnalysisProjectId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public AnalysisProjectId(Long value) {
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
        AnalysisProjectId that = (AnalysisProjectId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AnalysisProjectId{" + "value=" + value + '}';
    }
}
