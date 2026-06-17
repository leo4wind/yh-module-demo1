package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a CRF field option (CRF字段选项).
 * <p>
 * Wraps a {@link Long} identifier that references a pre-defined option for
 * SELECT, RADIO, or CHECKBOX field types. Each option has a label and value
 * pair that represents a valid choice for the field. Instances are immutable
 * and compared by their wrapped value.
 * </p>
 */
public class CrfFieldOptionId implements ValueObject {

    private final Long value;

    /**
     * Creates a new CrfFieldOptionId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public CrfFieldOptionId(Long value) {
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
        CrfFieldOptionId that = (CrfFieldOptionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CrfFieldOptionId{" + "value=" + value + '}';
    }
}
