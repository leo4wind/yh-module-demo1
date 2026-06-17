package com.clinicaltrial.ddd.trial.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event raised when a CRF template is bound to a trial stage.
 * <p>
 * This event signals that a CRF (Case Report Form) template, at a specific
 * version, has been associated with a stage. After binding, the CRF becomes
 * available for data entry during that stage. Consumers can use this to
 * initialize form instances or trigger data collection workflows.
 * </p>
 */
public class CrfBoundToStageEvent implements DomainEvent {

    private final StageId stageId;
    private final CrfTemplateId crfTemplateId;
    private final CrfVersionId crfVersionId;
    private final LocalDateTime occurredOn;

    /**
     * Creates a new CrfBoundToStageEvent.
     *
     * @param stageId       the stage to which the CRF was bound; must not be null
     * @param crfTemplateId the CRF template that was bound; must not be null
     * @param crfVersionId  the specific version of the CRF template; may be null
     * @param occurredOn    the timestamp of the event; must not be null
     */
    public CrfBoundToStageEvent(StageId stageId, CrfTemplateId crfTemplateId,
                                 CrfVersionId crfVersionId, LocalDateTime occurredOn) {
        this.stageId = Objects.requireNonNull(stageId, "stageId must not be null");
        this.crfTemplateId = Objects.requireNonNull(crfTemplateId, "crfTemplateId must not be null");
        this.crfVersionId = crfVersionId;
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    /**
     * Returns the stage identity.
     *
     * @return the stage ID
     */
    public StageId getStageId() {
        return stageId;
    }

    /**
     * Returns the CRF template identity.
     *
     * @return the CRF template ID
     */
    public CrfTemplateId getCrfTemplateId() {
        return crfTemplateId;
    }

    /**
     * Returns the CRF version identity (may be null for latest version).
     *
     * @return the CRF version ID, or null
     */
    public CrfVersionId getCrfVersionId() {
        return crfVersionId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "CrfBoundToStageEvent{stageId=" + stageId
                + ", crfTemplateId=" + crfTemplateId
                + ", version=" + crfVersionId + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfBoundToStageEvent that = (CrfBoundToStageEvent) o;
        return Objects.equals(stageId, that.stageId)
                && Objects.equals(crfTemplateId, that.crfTemplateId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stageId, crfTemplateId, occurredOn);
    }
}
