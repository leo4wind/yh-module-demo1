package com.clinicaltrial.ddd.subject.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the identity of a clinical trial project
 * (reference to the TrialConfiguration aggregate in BC1).
 * <p>
 * In DDD, a bounded context references another aggregate by its identity only.
 * This value object serves as that foreign key reference from Subject
 * (BC2) to Project (BC1).
 * </p>
 * <p>
 * Instances are immutable.
 * </p>
 */
public class ProjectId implements ValueObject {

    private final Long value;

    /**
     * Creates a new ProjectId.
     *
     * @param value the raw project identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public ProjectId(Long value) {
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
        ProjectId projectId = (ProjectId) o;
        return Objects.equals(value, projectId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ProjectId{" + "value=" + value + '}';
    }
}
