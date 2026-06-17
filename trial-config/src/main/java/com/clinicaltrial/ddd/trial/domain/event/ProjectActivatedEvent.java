package com.clinicaltrial.ddd.trial.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event raised when a clinical trial project transitions from
 * DRAFT to ACTIVE status.
 * <p>
 * This event signals that the trial has started. Configuration is now locked
 * and subject enrollment can begin. Consumers can use this to notify sites,
 * enable data collection modules, or start scheduling.
 * </p>
 */
public class ProjectActivatedEvent implements DomainEvent {

    private final ProjectId projectId;
    private final LocalDateTime occurredOn;

    /**
     * Creates a new ProjectActivatedEvent.
     *
     * @param projectId  the identity of the activated project; must not be null
     * @param occurredOn the timestamp of the event; must not be null
     */
    public ProjectActivatedEvent(ProjectId projectId, LocalDateTime occurredOn) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    /**
     * Returns the identity of the activated project.
     *
     * @return the project ID
     */
    public ProjectId getProjectId() {
        return projectId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ProjectActivatedEvent{projectId=" + projectId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectActivatedEvent that = (ProjectActivatedEvent) o;
        return Objects.equals(projectId, that.projectId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, occurredOn);
    }
}
