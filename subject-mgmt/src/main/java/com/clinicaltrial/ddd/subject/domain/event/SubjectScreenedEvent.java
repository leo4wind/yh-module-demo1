package com.clinicaltrial.ddd.subject.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Event raised when a subject has been screened.
 * <p>
 * This event is published after {@link
 * com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject#screen(
 * com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo)}
 * successfully transitions the subject to {@code SCREENING} status.
 * </p>
 */
public class SubjectScreenedEvent implements DomainEvent {

    private final SubjectId subjectId;
    private final ProjectId projectId;
    private final LocalDateTime occurredOn;

    /**
     * Constructs a new SubjectScreenedEvent.
     *
     * @param subjectId  the screened subject's identity
     * @param projectId  the project the subject belongs to
     * @param occurredOn the timestamp of the event
     */
    public SubjectScreenedEvent(SubjectId subjectId, ProjectId projectId, LocalDateTime occurredOn) {
        this.subjectId = subjectId;
        this.projectId = projectId;
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
     * Returns the project identity.
     *
     * @return the project identity
     */
    public ProjectId getProjectId() {
        return projectId;
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
        SubjectScreenedEvent that = (SubjectScreenedEvent) o;
        return Objects.equals(subjectId, that.subjectId)
                && Objects.equals(projectId, that.projectId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, projectId, occurredOn);
    }

    @Override
    public String toString() {
        return "SubjectScreenedEvent{"
                + "subjectId=" + subjectId
                + ", projectId=" + projectId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
