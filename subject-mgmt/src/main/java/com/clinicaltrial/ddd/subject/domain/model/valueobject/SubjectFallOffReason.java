package com.clinicaltrial.ddd.subject.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Value Object capturing the reason and date when a subject falls off
 * (terminates or withdraws from) the clinical trial.
 * <p>
 * Instances are immutable.
 * </p>
 */
public class SubjectFallOffReason implements ValueObject {

    private final String reasonCode;
    private final String reasonDescription;
    private final LocalDate fallOffDate;

    /**
     * Constructs a new SubjectFallOffReason.
     *
     * @param reasonCode        a short code identifying the reason (e.g. "ADVERSE_EVENT",
     *                          "WITHDRAWAL_OF_CONSENT"); must not be blank
     * @param reasonDescription a human-readable description of the reason; must not be blank
     * @param fallOffDate       the date the subject fell off; must not be null
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public SubjectFallOffReason(String reasonCode, String reasonDescription, LocalDate fallOffDate) {
        if (reasonCode == null || reasonCode.trim().isEmpty()) {
            throw new IllegalArgumentException("reasonCode must not be blank");
        }
        if (reasonDescription == null || reasonDescription.trim().isEmpty()) {
            throw new IllegalArgumentException("reasonDescription must not be blank");
        }
        if (fallOffDate == null) {
            throw new IllegalArgumentException("fallOffDate must not be null");
        }
        this.reasonCode = reasonCode.trim();
        this.reasonDescription = reasonDescription.trim();
        this.fallOffDate = fallOffDate;
    }

    /**
     * Returns the reason code.
     *
     * @return the reason code
     */
    public String getReasonCode() {
        return reasonCode;
    }

    /**
     * Returns the human-readable reason description.
     *
     * @return the reason description
     */
    public String getReasonDescription() {
        return reasonDescription;
    }

    /**
     * Returns the date the subject fell off.
     *
     * @return the fall-off date
     */
    public LocalDate getFallOffDate() {
        return fallOffDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubjectFallOffReason that = (SubjectFallOffReason) o;
        return Objects.equals(reasonCode, that.reasonCode)
                && Objects.equals(reasonDescription, that.reasonDescription)
                && Objects.equals(fallOffDate, that.fallOffDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reasonCode, reasonDescription, fallOffDate);
    }

    @Override
    public String toString() {
        return "SubjectFallOffReason{"
                + "reasonCode='" + reasonCode + '\''
                + ", reasonDescription='" + reasonDescription + '\''
                + ", fallOffDate=" + fallOffDate
                + '}';
    }
}
