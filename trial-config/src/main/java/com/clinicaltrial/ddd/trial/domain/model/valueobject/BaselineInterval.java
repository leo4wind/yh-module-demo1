package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing a baseline interval configuration for trial stages and visit plans.
 * <p>
 * A baseline interval defines a time offset relative to a reference date, expressed
 * as a numeric quantity and a time unit (DAY, WEEK, or MONTH). For example,
 * "14 DAYS" or "3 MONTHS". This is used to calculate follow-up visit dates
 * from a subject's baseline or source stage date.
 * </p>
 *
 * <p>
 * Instances are immutable and compared by their attributes.
 * </p>
 */
public class BaselineInterval implements ValueObject {

    private final Long interval;
    private final String normalizedUnit;

    /**
     * Creates a new BaselineInterval.
     *
     * @param interval the numeric quantity; must be non-null and non-negative
     * @param unit     the time unit; must be one of DAY, WEEK, or MONTH (case-insensitive)
     * @throws IllegalArgumentException if interval is null/negative or unit is invalid
     */
    public BaselineInterval(Long interval, String unit) {
        if (interval == null) {
            throw new IllegalArgumentException("BaselineInterval interval must not be null");
        }
        if (interval < 0) {
            throw new IllegalArgumentException("BaselineInterval interval must be non-negative, got: " + interval);
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new IllegalArgumentException("BaselineInterval unit must not be null or empty");
        }
        String norm = unit.trim().toUpperCase();
        if (!"DAY".equals(norm) && !"WEEK".equals(norm) && !"MONTH".equals(norm)) {
            throw new IllegalArgumentException("BaselineInterval unit must be DAY, WEEK, or MONTH, got: " + unit);
        }
        this.interval = interval;
        this.normalizedUnit = norm;
    }

    /**
     * Returns the numeric quantity of this interval.
     *
     * @return the interval value (non-negative)
     */
    public Long getInterval() {
        return interval;
    }

    /**
     * Returns the time unit of this interval.
     *
     * @return the normalized unit (DAY, WEEK, or MONTH)
     */
    public String getUnit() {
        return normalizedUnit;
    }

    /**
     * Converts this interval to its equivalent in days.
     * <p>
     * Conversion uses approximate equivalents: 1 WEEK = 7 days, 1 MONTH = 30 days.
     * For precise date calculations, use {@link java.time.Period} or similar
     * calendar-aware arithmetic.
     * </p>
     *
     * @return the approximate number of days represented by this interval
     */
    public long getIntervalInDays() {
        switch (normalizedUnit) {
            case "DAY":
                return interval;
            case "WEEK":
                return interval * 7L;
            case "MONTH":
                return interval * 30L;
            default:
                throw new IllegalStateException("Unexpected unit: " + normalizedUnit);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaselineInterval that = (BaselineInterval) o;
        return Objects.equals(interval, that.interval)
                && Objects.equals(normalizedUnit, that.normalizedUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interval, normalizedUnit);
    }

    @Override
    public String toString() {
        return "BaselineInterval{" + interval + " " + normalizedUnit + '}';
    }
}
