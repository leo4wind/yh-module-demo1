package com.clinicaltrial.ddd.trial.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.VisitPlanId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event raised when a visit plan is configured for a clinical trial project.
 * <p>
 * This event signals that a new visit plan has been set up, linking a source
 * stage to a target stage with defined timing intervals. Consumers can use this
 * to initialize schedule templates or notify data collection modules about the
 * planned visit schedule.
 * </p>
 */
public class VisitPlanConfiguredEvent implements DomainEvent {

    private final ProjectId projectId;
    private final VisitPlanId visitPlanId;
    private final StageId sourceStageId;
    private final StageId targetStageId;
    private final LocalDateTime occurredOn;

    /**
     * Creates a new VisitPlanConfiguredEvent.
     *
     * @param projectId    the project this visit plan belongs to; must not be null
     * @param visitPlanId  the identity of the configured visit plan; must not be null
     * @param sourceStageId the source stage for the visit; must not be null
     * @param targetStageId the target stage for the visit; must not be null
     * @param occurredOn   the timestamp of the event; must not be null
     */
    public VisitPlanConfiguredEvent(ProjectId projectId, VisitPlanId visitPlanId,
                                     StageId sourceStageId, StageId targetStageId,
                                     LocalDateTime occurredOn) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.visitPlanId = Objects.requireNonNull(visitPlanId, "visitPlanId must not be null");
        this.sourceStageId = Objects.requireNonNull(sourceStageId, "sourceStageId must not be null");
        this.targetStageId = Objects.requireNonNull(targetStageId, "targetStageId must not be null");
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
     * Returns the identity of the configured visit plan.
     *
     * @return the visit plan ID
     */
    public VisitPlanId getVisitPlanId() {
        return visitPlanId;
    }

    /**
     * Returns the source stage identity.
     *
     * @return the source stage ID
     */
    public StageId getSourceStageId() {
        return sourceStageId;
    }

    /**
     * Returns the target stage identity.
     *
     * @return the target stage ID
     */
    public StageId getTargetStageId() {
        return targetStageId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "VisitPlanConfiguredEvent{projectId=" + projectId
                + ", visitPlanId=" + visitPlanId
                + ", source=" + sourceStageId + ", target=" + targetStageId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VisitPlanConfiguredEvent that = (VisitPlanConfiguredEvent) o;
        return Objects.equals(projectId, that.projectId)
                && Objects.equals(visitPlanId, that.visitPlanId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId, visitPlanId, occurredOn);
    }
}
