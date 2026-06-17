package com.clinicaltrial.ddd.dataexport.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * ExportFilterId — 导出筛选条件标识值对象.
 * <p>
 * 包装Long类型，作为ExportFilter实体的唯一标识。
 * </p>
 */
public class ExportFilterId implements ValueObject {

    private final Long value;

    /**
     * 构造ExportFilterId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public ExportFilterId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExportFilterId value must not be null");
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
        ExportFilterId that = (ExportFilterId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ExportFilterId{" + "value=" + value + '}';
    }
}
