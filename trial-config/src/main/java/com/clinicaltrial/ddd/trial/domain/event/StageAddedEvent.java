package com.clinicaltrial.ddd.trial.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event raised when a new stage is added to a clinical trial project.
 * <p>
 * This event indicates that a trial stage (e.g., screening, treatment, follow-up)
 * has been configured for the project. Consumers can use this to trigger
 * default CRF bindings, notification to sites, or schedule templates.
 * </p>
 */
public class StageAddedEvent implements DomainEvent {

    private final ProjectId projectId;
    private final StageId stageId;
    private final String stageName;
    private final LocalDateTime occurredOn;

    /**
     * Creates a new StageAddedEvent.
     *
     * @param projectId  the project to which the stage was added; must not be null
     * @param stageId    the identity of the added stage; must not be null
     * @param stageName  the name of the added stage; must not be null
     * @param occurredOn the timestamp of the event; must not be null
     */
    public StageAddedEvent(ProjectId projectId, StageId stageId,
                            String stageName, LocalDateTime occurredOn) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.stageId = Objects.requireNonNull(stageId, "stageId must not be null");
        this.stageName = Objects.requireNonNull(stageName, "stageName must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    /**
     * Returns the project identity.
     *
     * @return the project ID
     */
    public ProjectId getProjectId() {
        return projectId;
    }

    /**
     * Returns the identity of the added stage.
     *
     * @return the stage ID
     */
    public StageId getStageId() {
        return stageId;
    }

    /**
     * Returns the name of the added stage.
     *
     * @return the stage name
     */
    public String getStageName() {
        return stageName;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "StageAddedEvent{projectId=" + projectId
                + ", stageId=" + stageId + ", name='" + stageName + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StageAddedEvent that = (StageAddedEvent) o;
        return Objects.equals(projectId, that.projectId)
                && Objects.equals(stageId, that.stageId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, stageId, occurredOn);
    }
}
