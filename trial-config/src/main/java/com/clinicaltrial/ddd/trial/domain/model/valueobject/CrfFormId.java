package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a CRF form (CRF表单).
 * <p>
 * Wraps a {@link Long} identifier that references a form within a CRF template.
 * A form groups related fields (e.g., "Vital Signs", "Medical History") into
 * a logical data entry unit. Instances are immutable and compared by their
 * wrapped value.
 * </p>
 */
public class CrfFormId implements ValueObject {

    private final Long value;

    /**
     * Creates a new CrfFormId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public CrfFormId(Long value) {
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
        CrfFormId that = (CrfFormId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CrfFormId{" + "value=" + value + '}';
    }
}
