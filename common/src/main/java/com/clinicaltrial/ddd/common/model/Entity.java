package com.clinicaltrial.ddd.common.model;

import java.util.Objects;

/**
 * DDD Entity base class.
 * Entities are defined by their identity, not their attributes.
 *
 * @param <ID> the type of the entity's identity
 */
public abstract class Entity<ID> {

    /**
     * Returns the entity's unique identity within its aggregate boundary.
     */
    public abstract ID getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(getId(), entity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
