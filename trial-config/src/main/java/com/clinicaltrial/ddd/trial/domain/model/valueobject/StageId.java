package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of a trial stage (研究阶段).
 * <p>
 * Wraps a {@link Long} identifier that references a stage within a clinical trial
 * project. Stages represent distinct phases such as screening, treatment, or
 * follow-up. Instances are immutable and compared by their wrapped value.
 * </p>
 */
public class StageId implements ValueObject {

    private final Long value;

    /**
     * Creates a new StageId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public StageId(Long value) {
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
        StageId stageId = (StageId) o;
        return Objects.equals(value, stageId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "StageId{" + "value=" + value + '}';
    }
}
