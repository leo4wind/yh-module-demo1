package com.clinicaltrial.ddd.trial.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain event raised when a new CRF template is created.
 * <p>
 * This event indicates that a CRF (Case Report Form) template has been
 * initialized in DRAFT status. Consumers can use this to trigger template
 * indexing, notification to form designers, or audit logging.
 * </p>
 */
public class CrfTemplateCreatedEvent implements DomainEvent {

    private final CrfTemplateId templateId;
    private final String name;
    private final LocalDateTime occurredOn;

    /**
     * Creates a new CrfTemplateCreatedEvent.
     *
     * @param templateId the identity of the created template; must not be null
     * @param name       the template name; must not be null
     * @param occurredOn the timestamp of the event; must not be null
     */
    public CrfTemplateCreatedEvent(CrfTemplateId templateId, String name,
                                    LocalDateTime occurredOn) {
        this.templateId = Objects.requireNonNull(templateId, "templateId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.occurredOn = Objects.requireNonNull(occurredOn, "occurredOn must not be null");
    }

    /**
     * Returns the identity of the created template.
     *
     * @return the template ID
     */
    public CrfTemplateId getTemplateId() {
        return templateId;
    }

    /**
     * Returns the template name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "CrfTemplateCreatedEvent{templateId=" + templateId
                + ", name='" + name + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CrfTemplateCreatedEvent that = (CrfTemplateCreatedEvent) o;
        return Objects.equals(templateId, that.templateId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, occurredOn);
    }
}
