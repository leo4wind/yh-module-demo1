package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a CRF template (CRF模板).
 * <p>
 * Wraps a {@link Long} identifier that references a CRF (Case Report Form)
 * template definition. CRF templates define the structure of data collection
 * forms used in clinical trials. Instances are immutable and compared by their
 * wrapped value.
 * </p>
 */
public class CrfTemplateId implements ValueObject {

    private final Long value;

    /**
     * Creates a new CrfTemplateId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public CrfTemplateId(Long value) {
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
        CrfTemplateId that = (CrfTemplateId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "CrfTemplateId{" + "value=" + value + '}';
    }
}
