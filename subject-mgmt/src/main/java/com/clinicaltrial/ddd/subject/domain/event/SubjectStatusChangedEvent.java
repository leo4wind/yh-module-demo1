package com.clinicaltrial.ddd.subject.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Event raised when a subject's lifecycle status has changed
 * for reasons other than screening, enrollment, completion, or withdrawal.
 * <p>
 * This is used primarily for the ACTIVE → TERMINATED transition, as well as
 * the ENROLLED → ACTIVE activation transition. It carries both the old and
 * new status values along with a textual reason.
 * </p>
 */
public class SubjectStatusChangedEvent implements DomainEvent {

    private final SubjectId subjectId;
    private final SubjectStatus oldStatus;
    private final SubjectStatus newStatus;
    private final String reason;
    private final LocalDateTime occurredOn;

    /**
     * Constructs a new SubjectStatusChangedEvent.
     *
     * @param subjectId  the subject whose status changed
     * @param oldStatus  the status before the change
     * @param newStatus  the status after the change
     * @param reason     a textual description of why the status changed
     * @param occurredOn the timestamp of the event
     */
    public SubjectStatusChangedEvent(SubjectId subjectId,
                                      SubjectStatus oldStatus,
                                      SubjectStatus newStatus,
                                      String reason,
                                      LocalDateTime occurredOn) {
        this.subjectId = subjectId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
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
     * Returns the status before the change.
     *
     * @return the old status
     */
    public SubjectStatus getOldStatus() {
        return oldStatus;
    }

    /**
     * Returns the status after the change.
     *
     * @return the new status
     */
    public SubjectStatus getNewStatus() {
        return newStatus;
    }

    /**
     * Returns the reason for the status change.
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
        SubjectStatusChangedEvent that = (SubjectStatusChangedEvent) o;
        return Objects.equals(subjectId, that.subjectId)
                && oldStatus == that.oldStatus
                && newStatus == that.newStatus
                && Objects.equals(reason, that.reason)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, oldStatus, newStatus, reason, occurredOn);
    }

    @Override
    public String toString() {
        return "SubjectStatusChangedEvent{"
                + "subjectId=" + subjectId
                + ", oldStatus=" + oldStatus
                + ", newStatus=" + newStatus
                + ", reason='" + reason + '\''
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
