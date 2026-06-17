package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a CRF template version (CRF版本).
 * <p>
 * Wraps a {@link Long} identifier that references a specific version of a CRF
 * template. Versioning allows tracking changes to CRF forms over time while
 * maintaining data integrity for already-collected data. Instances are immutable
 * and compared by their wrapped value.
 * </p>
 */
public class CrfVersionId implements ValueObject {

    private final Long value;

    /**
     * Creates a new CrfVersionId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public CrfVersionId(Long value) {
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
        CrfVersionId that = (CrfVersionId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CrfVersionId{" + "value=" + value + '}';
    }
}
