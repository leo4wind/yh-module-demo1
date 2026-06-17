package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Date;
import java.util.Objects;

/**
 * BaselineTime — 基线时间值对象.
 * <p>
 * 表示SubjectStage的基线时间信息，包含：
 * <ul>
 *   <li>baselineDate — 基线日期</li>
 *   <li>source — 基线数据来源描述（如"visit"、"calculation"等）</li>
 * </ul>
 * </p>
 */
public class BaselineTime implements ValueObject {

    private final Date baselineDate;
    private final String source;

    /**
     * 构造BaselineTime.
     *
     * @param baselineDate 基线日期，不能为空
     * @param source       基线数据来源，不能为空
     * @throws IllegalArgumentException 如果baselineDate或source为null
     */
    public BaselineTime(Date baselineDate, String source) {
        if (baselineDate == null) {
            throw new IllegalArgumentException("BaselineDate must not be null");
        }
        if (source == null) {
            throw new IllegalArgumentException("Source must not be null");
        }
        this.baselineDate = (Date) baselineDate.clone();
        this.source = source;
    }

    /**
     * 获取基线日期.
     *
     * @return 基线日期（防御性拷贝）
     */
    public Date getBaselineDate() {
        return (Date) baselineDate.clone();
    }

    /**
     * 获取基线数据来源.
     *
     * @return 来源描述
     */
    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaselineTime that = (BaselineTime) o;
        return Objects.equals(baselineDate, that.baselineDate)
                && Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baselineDate, source);
    }

    @Override
    public String toString() {
        return "BaselineTime{" + "baselineDate=" + baselineDate
                + ", source='" + source + '\'' + '}';
    }
}
