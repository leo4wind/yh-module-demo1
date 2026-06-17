package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a visit plan (访视计划).
 * <p>
 * Wraps a {@link Long} identifier that references a visit plan configuration
 * within a project. Visit plans define the timing of patient follow-up visits
 * relative to a source stage. Instances are immutable and compared by their
 * wrapped value.
 * </p>
 */
public class VisitPlanId implements ValueObject {

    private final Long value;

    /**
     * Creates a new VisitPlanId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public VisitPlanId(Long value) {
        this.value = value;
    }

    /**
     * Returns the raw identifier value.
     *
     * @return the wrapped Long value
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
