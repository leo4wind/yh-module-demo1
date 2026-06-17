package com.clinicaltrial.ddd.trial.domain.model.valueobject;

import com.clinicaltrial.ddd.common.model.ValueObject;

import java.util.Objects;

/**
 * Value Object representing the unique identity of an adverse event rule (不良事件规则).
 * <p>
 * Wraps a {@link Long} identifier that references a rule for automatically
 * detecting adverse events based on CRF field values or logical expressions.
 * Instances are immutable and compared by their wrapped value.
 * </p>
 */
public class AdverseEventRuleId implements ValueObject {

    private final Long value;

    /**
     * Creates a new AdverseEventRuleId.
     *
     * @param value the raw identifier; must not be null
     * @throws IllegalArgumentException if value is null
     */
    public AdverseEventRuleId(Long value) {
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
        AdverseEventRuleId that = (AdverseEventRuleId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AdverseEventRuleId{" + "value=" + value + '}';
    }
}
