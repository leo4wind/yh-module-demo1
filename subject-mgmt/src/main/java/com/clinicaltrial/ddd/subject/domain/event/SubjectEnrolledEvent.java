package com.clinicaltrial.ddd.subject.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Domain Event raised when a subject has been enrolled into a clinical trial.
 * <p>
 * This event is published after {@link
 * com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject#enroll()}
 * successfully transitions the subject from {@code null} or
 * {@code SCREENING} to {@code ENROLLED} status.
 * </p>
 */
public class SubjectEnrolledEvent implements DomainEvent {

    private final SubjectId subjectId;
    private final ProjectId projectId;
    private final SubjectCode subjectCode;
    private final LocalDateTime occurredOn;

    /**
     * Constructs a new SubjectEnrolledEvent.
     *
     * @param subjectId   the enrolled subject's identity
     * @param projectId   the project the subject is enrolled in
     * @param subjectCode the generated enrollment code
     * @param occurredOn  the timestamp of the event
     */
    public SubjectEnrolledEvent(SubjectId subjectId,
                                 ProjectId projectId,
                                 SubjectCode subjectCode,
                                 LocalDateTime occurredOn) {
        this.subjectId = subjectId;
        this.projectId = projectId;
        this.subjectCode = subjectCode;
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

    /**
     * Returns the enrollment code assigned to the subject.
     *
     * @return the subject code
     */
    public SubjectCode getSubjectCode() {
        return subjectCode;
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
        SubjectEnrolledEvent that = (SubjectEnrolledEvent) o;
        return Objects.equals(subjectId, that.subjectId)
                && Objects.equals(projectId, that.projectId)
                && Objects.equals(subjectCode, that.subjectCode)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectId, projectId, subjectCode, occurredOn);
    }

    @Override
    public String toString() {
        return "SubjectEnrolledEvent{"
                + "subjectId=" + subjectId
                + ", projectId=" + projectId
                + ", subjectCode=" + subjectCode
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
