package com.clinicaltrial.ddd.subject.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Event raised when a subject has voluntarily withdrawn from the trial.
 * <p>
 * This event is published after {@link
 * com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject#withdraw(
 * com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectFallOffReason)}
 * successfully transitions the subject from {@code SCREENING} or
 * {@code ACTIVE} to {@code WITHDRAWN} status.
 * </p>
 */
public class SubjectWithdrawnEvent implements DomainEvent {

    private final SubjectId subjectId;
    private final String reason;
    private final LocalDateTime occurredOn;

    /**
     * Constructs a new SubjectWithdrawnEvent.
     *
     * @param subjectId  the subject who withdrew
     * @param reason     a textual description of the withdrawal reason
     * @param occurredOn the timestamp of the event
     */
    public SubjectWithdrawnEvent(SubjectId subjectId, String reason, LocalDateTime occurredOn) {
        this.subjectId = subjectId;
        this.reason = reason;
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

    /**
     * Returns the withdrawal reason.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
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
        SubjectWithdrawnEvent that = (SubjectWithdrawnEvent) o;
        return Objects.equals(subjectId, that.subjectId)
                && Objects.equals(reason, that.reason)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, reason, occurredOn);
    }

    @Override
    public String toString() {
        return "SubjectWithdrawnEvent{"
                + "subjectId=" + subjectId
                + ", reason='" + reason + '\''
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
