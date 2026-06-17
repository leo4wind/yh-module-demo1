package com.clinicaltrial.ddd.subject.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value Object capturing the screening evaluation information for a subject.
 * <p>
 * Records the date of screening, the result (PASS or FAIL), and any
 * additional remarks provided by the investigator.
 * </p>
 * <p>
 * Instances are immutable.
 * </p>
 */
public class ScreeningInfo implements ValueObject {

    private final LocalDate screeningDate;
    private final ScreeningResult screeningResult;
    private final String remarks;

    /**
     * Constructs a new ScreeningInfo.
     *
     * @param screeningDate   the date on which screening was performed; must not be null
     * @param screeningResult the result of the screening evaluation; must not be null
     * @param remarks         optional remarks (may be null or empty)
     * @throws IllegalArgumentException if screeningDate or screeningResult is null
     */
    public ScreeningInfo(LocalDate screeningDate, ScreeningResult screeningResult, String remarks) {
        if (screeningDate == null) {
            throw new IllegalArgumentException("screeningDate must not be null");
        }
        if (screeningResult == null) {
            throw new IllegalArgumentException("screeningResult must not be null");
        }
        this.screeningDate = screeningDate;
        this.screeningResult = screeningResult;
        this.remarks = remarks;
    }

    /**
     * Returns the screening date.
     *
     * @return the screening date
     */
    public LocalDate getScreeningDate() {
        return screeningDate;
    }

    /**
     * Returns the screening result.
     *
     * @return the screening result
     */
    public ScreeningResult getScreeningResult() {
        return screeningResult;
    }

    /**
     * Returns any remarks associated with the screening.
     *
     * @return the remarks, or null if none were provided
     */
    public String getRemarks() {
        return remarks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ScreeningInfo that = (ScreeningInfo) o;
        return Objects.equals(screeningDate, that.screeningDate)
                && screeningResult == that.screeningResult
                && Objects.equals(remarks, that.remarks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screeningDate, screeningResult, remarks);
    }

    @Override
    public String toString() {
        return "ScreeningInfo{"
                + "screeningDate=" + screeningDate
                + ", screeningResult=" + screeningResult
                + ", remarks='" + remarks + '\''
                + '}';
    }

    /**
     * Enum representing the outcome of a subject screening evaluation.
     */
    public enum ScreeningResult {
        PASS,
        FAIL
    }
}
