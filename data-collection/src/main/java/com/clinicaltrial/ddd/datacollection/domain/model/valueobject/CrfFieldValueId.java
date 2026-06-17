package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * CrfFieldValueId — CRF字段值标识值对象.
 * <p>
 * 包装Long类型，作为CrfFieldValue实体的唯一标识。
 * </p>
 */
public class CrfFieldValueId implements ValueObject {

    private final Long value;

    /**
     * 构造CrfFieldValueId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public CrfFieldValueId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("CrfFieldValueId value must not be null");
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
        CrfFieldValueId that = (CrfFieldValueId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CrfFieldValueId{" + "value=" + value + '}';
    }
}
