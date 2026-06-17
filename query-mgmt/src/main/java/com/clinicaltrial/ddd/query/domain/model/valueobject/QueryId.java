package com.clinicaltrial.ddd.query.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * QueryId — 质疑标识值对象.
 * <p>
 * 包装Long类型，作为Query聚合的唯一标识。
 * 用于在质疑管理上下文中唯一定位一条质疑记录。
 * </p>
 */
public class QueryId implements ValueObject {

    private final Long value;

    /**
     * 构造QueryId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public QueryId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("QueryId value must not be null");
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
        QueryId queryId = (QueryId) o;
        return Objects.equals(value, queryId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "QueryId{" + "value=" + value + '}';
    }
}
