package com.clinicaltrial.ddd.dataexport.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * ExportFieldConfigId — 导出字段配置标识值对象.
 * <p>
 * 包装Long类型，作为ExportFieldConfig实体的唯一标识。
 * </p>
 */
public class ExportFieldConfigId implements ValueObject {

    private final Long value;

    /**
     * 构造ExportFieldConfigId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public ExportFieldConfigId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExportFieldConfigId value must not be null");
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
        ExportFieldConfigId that = (ExportFieldConfigId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ExportFieldConfigId{" + "value=" + value + '}';
    }
}
