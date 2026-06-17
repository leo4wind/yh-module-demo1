package com.clinicaltrial.ddd.common.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DDD Domain Event base interface.
 * Domain events represent something meaningful that happened in the domain.
 * They are immutable and carry the timestamp of occurrence.
 */
public interface DomainEvent extends Serializable {

    /**
     * When the event occurred.
     */
    LocalDateTime occurredOn();

    /**
     * A human-readable description of the event (for logging/audit).
     */
    default String description() {
        return getClass().getSimpleName() + " occurred at " + occurredOn();
    }
}
