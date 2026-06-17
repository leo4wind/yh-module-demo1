package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Value Object representing a window period for visit plan timing.
 * <p>
 * A window period defines the acceptable time range around a scheduled visit
 * date. It consists of a number of days before ({@code beforeDays}) and after
 * ({@code afterDays}) the target date within which the visit must occur to be
 * considered on-schedule.
 * </p>
 *
 * <p>
 * For example, a window of {@code beforeDays=3, afterDays=7} means a visit
 * scheduled for day 14 can occur between day 11 and day 21.
 * </p>
 *
 * <p>
 * Instances are immutable and compared by their attributes.
 * </p>
 */
public class WindowPeriod implements ValueObject {

    private final Long beforeDays;
    private final Long afterDays;

    /**
     * Creates a new WindowPeriod.
     *
     * @param beforeDays the number of days allowed before the target date;
     *                   must be non-null and non-negative
     * @param afterDays  the number of days allowed after the target date;
     *                   must be non-null and non-negative
     * @throws IllegalArgumentException if either parameter is null or negative
     */
    public WindowPeriod(Long beforeDays, Long afterDays) {
        if (beforeDays == null) {
            throw new IllegalArgumentException("WindowPeriod beforeDays must not be null");
        }
        if (afterDays == null) {
            throw new IllegalArgumentException("WindowPeriod afterDays must not be null");
        }
        if (beforeDays < 0) {
            throw new IllegalArgumentException("WindowPeriod beforeDays must be non-negative, got: " + beforeDays);
        }
        if (afterDays < 0) {
            throw new IllegalArgumentException("WindowPeriod afterDays must be non-negative, got: " + afterDays);
        }
        this.beforeDays = beforeDays;
        this.afterDays = afterDays;
    }

    /**
     * Returns the number of days allowed before the target date.
     *
     * @return the before-days window
     */
    public Long getBeforeDays() {
        return beforeDays;
    }

    /**
     * Returns the number of days allowed after the target date.
     *
     * @return the after-days window
     */
    public Long getAfterDays() {
        return afterDays;
    }

    /**
     * Checks whether a given date falls within this window period relative to
     * a target date.
     * <p>
     * A date is considered within the window if:
     * <pre>
     * targetDate - beforeDays &lt;= date &lt;= targetDate + afterDays
     * </pre>
     * </p>
     *
     * @param targetDate the reference (scheduled) date; must not be null
     * @param date       the date to check; must not be null
     * @return {@code true} if the date is within the acceptable range
     * @throws IllegalArgumentException if either parameter is null
     */
    public boolean contains(Date targetDate, Date date) {
        if (targetDate == null) {
            throw new IllegalArgumentException("targetDate must not be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("date must not be null");
        }

        long targetMillis = targetDate.getTime();
        long dateMillis = date.getTime();
        long dayMillis = TimeUnit.DAYS.toMillis(1);

        long windowStart = targetMillis - (beforeDays * dayMillis);
        long windowEnd = targetMillis + (afterDays * dayMillis);

        return dateMillis >= windowStart && dateMillis <= windowEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WindowPeriod that = (WindowPeriod) o;
        return Objects.equals(beforeDays, that.beforeDays)
                && Objects.equals(afterDays, that.afterDays);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beforeDays, afterDays);
    }

    @Override
    public String toString() {
        return "WindowPeriod{±" + beforeDays + "/+" + afterDays + " days}";
    }
}
