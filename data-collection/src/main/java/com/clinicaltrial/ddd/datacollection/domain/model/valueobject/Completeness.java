package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Completeness — CRF评估完整性值对象.
 * <p>
 * 表示CRF评估记录的填写完整性信息，包含：
 * <ul>
 *   <li>percentage — 完成百分比（0-100的BigDecimal）</li>
 *   <li>filledCount — 已填写的必填字段数</li>
 *   <li>totalCount — 总的必填字段数</li>
 * </ul>
 * 当百分比>=100时视为完成。
 * </p>
 */
public class Completeness implements ValueObject {

    private final BigDecimal percentage;
    private final int filledCount;
    private final int totalCount;

    /**
     * 构造完整性为0%的值对象.
     *
     * @return 0%完整性的Completeness实例
     */
    public static Completeness zero() {
        return new Completeness(BigDecimal.ZERO, 0, 0);
    }

    /**
     * 构造Completeness.
     *
     * @param percentage  完成百分比（0-100）
     * @param filledCount 已填写的必填字段数
     * @param totalCount  总的必填字段数
     * @throws IllegalArgumentException 如果percentage不在0-100范围内，
     *                                  或filledCount > totalCount，
     *                                  或任一参数为负
     */
    public Completeness(BigDecimal percentage, int filledCount, int totalCount) {
        if (percentage == null) {
            throw new IllegalArgumentException("Percentage must not be null");
        }
        if (percentage.compareTo(BigDecimal.ZERO) < 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentage must be between 0 and 100");
        }
        if (filledCount < 0 || totalCount < 0) {
            throw new IllegalArgumentException("FilledCount and totalCount must not be negative");
        }
        if (filledCount > totalCount) {
            throw new IllegalArgumentException("FilledCount must not exceed totalCount");
        }
        this.percentage = percentage.setScale(2, RoundingMode.HALF_UP);
        this.filledCount = filledCount;
        this.totalCount = totalCount;
    }

    /**
     * 获取完成百分比.
     *
     * @return 百分比值（0-100）
     */
    public BigDecimal getPercentage() {
        return percentage;
    }

    /**
     * 获取已填写的必填字段数.
     *
     * @return 已填写数量
     */
    public int getFilledCount() {
        return filledCount;
    }

    /**
     * 获取总的必填字段数.
     *
     * @return 总数量
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * 判断是否完成.
     * <p>
     * 当百分比 >= 100 时视为完成。
     * </p>
     *
     * @return true 如果已完成
     */
    public boolean isComplete() {
        return percentage.compareTo(new BigDecimal("100")) >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Completeness that = (Completeness) o;
        return filledCount == that.filledCount
                && totalCount == that.totalCount
                && Objects.equals(percentage, that.percentage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(percentage, filledCount, totalCount);
    }

    @Override
    public String toString() {
        return "Completeness{" + "percentage=" + percentage
                + "%, filledCount=" + filledCount
                + ", totalCount=" + totalCount + '}';
    }
}
