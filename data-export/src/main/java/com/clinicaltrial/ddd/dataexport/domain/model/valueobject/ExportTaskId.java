package com.clinicaltrial.ddd.dataexport.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * ExportTaskId — 导出任务标识值对象.
 * <p>
 * 包装Long类型，作为ExportTask聚合的唯一标识。
 * 用于在数据导出上下文中唯一定位一个导出任务。
 * </p>
 */
public class ExportTaskId implements ValueObject {

    private final Long value;

    /**
     * 构造ExportTaskId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public ExportTaskId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("ExportTaskId value must not be null");
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
        ExportTaskId that = (ExportTaskId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ExportTaskId{" + "value=" + value + '}';
    }
}
