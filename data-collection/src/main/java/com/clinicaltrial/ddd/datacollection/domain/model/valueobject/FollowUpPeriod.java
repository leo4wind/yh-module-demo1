package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Date;
import java.util.Objects;

/**
 * FollowUpPeriod — 随访周期值对象.
 * <p>
 * 表示SubjectStage的随访时间窗口，包含：
 * <ul>
 *   <li>followStartAt — 随访开始日期</li>
 *   <li>followEndAt — 随访结束日期</li>
 * </ul>
 * </p>
 */
public class FollowUpPeriod implements ValueObject {

    private final Date followStartAt;
    private final Date followEndAt;

    /**
     * 构造FollowUpPeriod.
     *
     * @param followStartAt 随访开始日期，不能为空
     * @param followEndAt   随访结束日期，不能为空，必须晚于或等于followStartAt
     * @throws IllegalArgumentException 如果任一参数为null或followEndAt早于followStartAt
     */
    public FollowUpPeriod(Date followStartAt, Date followEndAt) {
        if (followStartAt == null) {
            throw new IllegalArgumentException("FollowStartAt must not be null");
        }
        if (followEndAt == null) {
            throw new IllegalArgumentException("FollowEndAt must not be null");
        }
        if (followEndAt.before(followStartAt)) {
            throw new IllegalArgumentException("FollowEndAt must not be before followStartAt");
        }
        this.followStartAt = (Date) followStartAt.clone();
        this.followEndAt = (Date) followEndAt.clone();
    }

    /**
     * 获取随访开始日期.
     *
     * @return 开始日期（防御性拷贝）
     */
    public Date getFollowStartAt() {
        return (Date) followStartAt.clone();
    }

    /**
     * 获取随访结束日期.
     *
     * @return 结束日期（防御性拷贝）
     */
    public Date getFollowEndAt() {
        return (Date) followEndAt.clone();
    }

    /**
     * 判断指定日期是否在随访周期内.
     *
     * @param date 待检查日期
     * @return true 如果在周期内（含边界）
     */
    public boolean contains(Date date) {
        if (date == null) {
            return false;
        }
        return !date.before(followStartAt) && !date.after(followEndAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FollowUpPeriod that = (FollowUpPeriod) o;
        return Objects.equals(followStartAt, that.followStartAt)
                && Objects.equals(followEndAt, that.followEndAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(followStartAt, followEndAt);
    }

    @Override
    public String toString() {
        return "FollowUpPeriod{" + "followStartAt=" + followStartAt
                + ", followEndAt=" + followEndAt + '}';
    }
}
