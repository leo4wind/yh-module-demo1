package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a CRF field (CRF字段).
 * <p>
 * Wraps a {@link Long} identifier that references a single data field within a
 * CRF form. Fields capture individual data points such as temperature readings,
 * medication names, or lab results. Instances are immutable and compared by
 * their wrapped value.
 * </p>
 */
public class CrfFieldId implements ValueObject {

    private final Long value;

    /**
     * Creates a new CrfFieldId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public CrfFieldId(Long value) {
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
        CrfFieldId that = (CrfFieldId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CrfFieldId{" + "value=" + value + '}';
    }
}
