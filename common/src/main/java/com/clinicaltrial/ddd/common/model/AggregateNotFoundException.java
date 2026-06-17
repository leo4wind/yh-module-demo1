package com.clinicaltrial.ddd.common.model;

/**
 * Thrown when an aggregate cannot be found by its identity.
 */
public class AggregateNotFoundException extends DomainException {

    public AggregateNotFoundException(String aggregateType, Object id) {
        super("AGGREGATE_NOT_FOUND",
              String.format("%s with id [%s] not found", aggregateType, id));
    }
}
