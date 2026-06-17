package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a stage-to-CRF binding (阶段CRF绑定).
 * <p>
 * Wraps a {@link Long} identifier that references a binding between a trial stage
 * and a CRF template version. This binding determines which CRF forms are
 * available for data entry in a given stage. Instances are immutable and compared
 * by their wrapped value.
 * </p>
 */
public class StageCrfBindingId implements ValueObject {

    private final Long value;

    /**
     * Creates a new StageCrfBindingId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public StageCrfBindingId(Long value) {
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
        StageCrfBindingId that = (StageCrfBindingId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "StageCrfBindingId{" + "value=" + value + '}';
    }
}
