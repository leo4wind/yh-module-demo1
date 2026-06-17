package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * VisitPlanId — 访视计划标识值对象（数据采集上下文本地副本）.
 * <p>
 * 包装Long类型，引用BC1（试验配置）的访视计划聚合。
 * 在DDD限界上下文中，每个BC维护自己的引用类型副本。
 * </p>
 */
public class VisitPlanId implements ValueObject {

    private final Long value;

    /**
     * 构造VisitPlanId.
     *
     * @param value 原始Long值，不能为空
     * @throws IllegalArgumentException 如果value为null
     */
    public VisitPlanId(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("VisitPlanId value must not be null");
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
        VisitPlanId that = (VisitPlanId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "VisitPlanId{" + "value=" + value + '}';
    }
}
