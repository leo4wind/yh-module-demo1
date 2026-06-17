package com.clinicaltrial.ddd.subject.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Event raised when a subject has completed the clinical trial per protocol.
 * <p>
 * This event is published after {@link
 * com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject#complete()}
 * successfully transitions the subject from {@code ACTIVE} to
 * {@code COMPLETED} status.
 * </p>
 */
public class SubjectCompletedEvent implements DomainEvent {

    private final SubjectId subjectId;
    private final LocalDateTime occurredOn;

    /**
     * Constructs a new SubjectCompletedEvent.
     *
     * @param subjectId  the subject who completed the trial
     * @param occurredOn the timestamp of the event
     */
    public SubjectCompletedEvent(SubjectId subjectId, LocalDateTime occurredOn) {
        this.subjectId = subjectId;
        this.occurredOn = occurredOn;
    }

    /**
     * Returns the subject identity.
     *
     * @return the subject identity
     */
    public SubjectId getSubjectId() {
        return subjectId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubjectCompletedEvent that = (SubjectCompletedEvent) o;
        return Objects.equals(subjectId, that.subjectId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, occurredOn);
    }

    @Override
    public String toString() {
        return "SubjectCompletedEvent{"
                + "subjectId=" + subjectId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
