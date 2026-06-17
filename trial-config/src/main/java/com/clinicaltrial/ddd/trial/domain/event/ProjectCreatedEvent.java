package com.clinicaltrial.ddd.trial.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event raised when a new clinical trial project is created.
 * <p>
 * This event indicates that a project has been initialized in DRAFT status
 * and is ready for configuration. Consumers can use this to trigger
 * provisioning of project resources, notifications, or audit logging.
 * </p>
 */
public class ProjectCreatedEvent implements DomainEvent {

    private final ProjectId projectId;
    private final String title;
    private final String createUserId;
    private final LocalDateTime occurredOn;

    /**
     * Creates a new ProjectCreatedEvent.
     *
     * @param projectId    the identity of the created project; must not be null
     * @param title        the project title; must not be null
     * @param createUserId the user who created the project; must not be null
     * @param occurredOn   the timestamp of the event; must not be null
     */
    public ProjectCreatedEvent(ProjectId projectId, String title,
                                String createUserId, LocalDateTime occurredOn) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.title = Objects.requireNonNull(title, "title must not be null");
        this.createUserId = Objects.requireNonNull(createUserId, "createUserId must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    /**
     * Returns the identity of the created project.
     *
     * @return the project ID
     */
    public ProjectId getProjectId() {
        return projectId;
    }

    /**
     * Returns the project title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the user who created the project.
     *
     * @return the creator user ID
     */
    public String getCreateUserId() {
        return createUserId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "ProjectCreatedEvent{projectId=" + projectId
                + ", title='" + title + "', userId=" + createUserId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectCreatedEvent that = (ProjectCreatedEvent) o;
        return Objects.equals(projectId, that.projectId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, occurredOn);
    }
}
