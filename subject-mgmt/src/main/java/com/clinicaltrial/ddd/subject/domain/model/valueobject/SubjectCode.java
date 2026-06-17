package com.clinicaltrial.ddd.subject.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing a human-readable subject enrollment code.
 * <p>
 * Format: {@code "{projectPrefix}-{seq}"} where the sequence portion is padded
 * to at least 4 digits, e.g. {@code "TRIAL-0001"} or {@code "TRIAL-0042"}.
 * </p>
 * <p>
 * Instances are immutable.
 * </p>
 */
public class SubjectCode implements ValueObject {

    private final String projectPrefix;
    private final int sequenceNumber;

    /**
     * Constructs a SubjectCode.
     *
     * @param projectPrefix  the project prefix (e.g. "TRIAL"); must not be blank
     * @param sequenceNumber the sequential number within the project; must be &gt;= 1
     * @throws IllegalArgumentException if projectPrefix is blank or sequenceNumber &lt; 1
     */
    public SubjectCode(String projectPrefix, int sequenceNumber) {
        if (projectPrefix == null || projectPrefix.trim().isEmpty()) {
            throw new IllegalArgumentException("projectPrefix must not be blank");
        }
        if (sequenceNumber < 1) {
            throw new IllegalArgumentException("sequenceNumber must be >= 1, got " + sequenceNumber);
        }
        this.projectPrefix = projectPrefix.trim();
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * Returns the full formatted code string, e.g. "TRIAL-0001".
     *
     * @return the formatted code
     */
    public String getFullCode() {
        return projectPrefix + "-" + String.format("%04d", sequenceNumber);
    }

    /**
     * Returns the project prefix portion of the code.
     *
     * @return the project prefix (e.g. "TRIAL")
     */
    public String getProjectPrefix() {
        return projectPrefix;
    }

    /**
     * Returns the sequence number portion of the code.
     *
     * @return the sequence number
     */
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubjectCode that = (SubjectCode) o;
        return sequenceNumber == that.sequenceNumber
                && Objects.equals(projectPrefix, that.projectPrefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectPrefix, sequenceNumber);
    }

    @Override
    public String toString() {
        return getFullCode();
    }
}
