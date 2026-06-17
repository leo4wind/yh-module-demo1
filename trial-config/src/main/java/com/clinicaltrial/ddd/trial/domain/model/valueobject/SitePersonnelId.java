package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a site personnel assignment (中心人员).
 * <p>
 * Wraps a {@link Long} identifier that references an assignment of a user to a
 * trial site with a specific role (PI, Sub-investigator, CRC, CRA, Auditor).
 * Instances are immutable and compared by their wrapped value.
 * </p>
 */
public class SitePersonnelId implements ValueObject {

    private final Long value;

    /**
     * Creates a new SitePersonnelId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public SitePersonnelId(Long value) {
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
        SitePersonnelId that = (SitePersonnelId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "SitePersonnelId{" + "value=" + value + '}';
    }
}
