package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a clinical trial project.
 * <p>
 * Wraps a {@link Long} identifier that references the project aggregate in BC1 (Trial Configuration).
 * Instances are immutable and compared by their wrapped value.
 * </p>
 */
public class ProjectId implements ValueObject {

    private final Long value;

    /**
     * Creates a new ProjectId.
     *
     * @param value the raw identifier; may be null for new entities before persistence
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
