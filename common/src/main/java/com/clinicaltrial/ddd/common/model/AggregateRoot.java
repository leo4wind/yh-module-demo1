package com.clinicaltrial.ddd.common.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * DDD Aggregate Root base class.
 * Manages domain events collection and provides identity comparison.
 *
 * @param <ID> the type of the aggregate's identity
 */
public abstract class AggregateRoot<ID extends ValueObject> {

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    /**
     * Returns the aggregate's unique identity.
     */
    public abstract ID getId();

    /**
     * Register a domain event to be published after the aggregate is persisted.
     */
    protected void registerEvent(DomainEvent event) {
        if (event != null) {
            this.domainEvents.add(event);
        }
    }

    /**
     * Returns an unmodifiable view of pending domain events, then clears them.
     * Called by repository after successful persistence.
     */
    public List<DomainEvent> pullDomainEvents() {
        if (domainEvents.isEmpty()) {
            return Collections.emptyList();
        }
        List<DomainEvent> events = new ArrayList<>(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
    }

    /**
     * Clear pending domain events without publishing (e.g., on persistence failure).
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRoot<?> that = (AggregateRoot<?>) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
