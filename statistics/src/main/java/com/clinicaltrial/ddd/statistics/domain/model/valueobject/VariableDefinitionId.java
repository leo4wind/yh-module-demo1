package com.clinicaltrial.ddd.statistics.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * VariableDefinitionId — 变量定义标识值对象.
 * <p>
 * 包装Long类型，作为VariableDefinition实体的唯一标识。
 * </p>
 */
public class VariableDefinitionId implements ValueObject {

    private final Long value;

    /**
     * 构造VariableDefinitionId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public VariableDefinitionId(Long value) {
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
        VariableDefinitionId that = (VariableDefinitionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "VariableDefinitionId{" + "value=" + value + '}';
    }
}
