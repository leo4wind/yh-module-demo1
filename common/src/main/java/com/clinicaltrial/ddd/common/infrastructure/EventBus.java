package com.clinicaltrial.ddd.common.infrastructure;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Domain Event Bus backed by Spring's ApplicationEventPublisher.
 * Aggregates publish events via this bus; subscribers use @EventListener.
 */
@Component
public class EventBus {

    private final ApplicationEventPublisher publisher;

    public EventBus(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Publish a single domain event.
     */
    public void publish(DomainEvent event) {
        if (event != null) {
            publisher.publishEvent(event);
        }
    }

    /**
     * Publish all pending domain events from an aggregate root,
     * then clear the aggregate's event collection.
     */
    public void publishAll(com.clinicaltrial.ddd.common.model.AggregateRoot<?> aggregate) {
        if (aggregate != null) {
            for (DomainEvent event : aggregate.pullDomainEvents()) {
                publisher.publishEvent(event);
            }
        }
    }
}
