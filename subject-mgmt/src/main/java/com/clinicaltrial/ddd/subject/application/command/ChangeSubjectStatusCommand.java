package com.clinicaltrial.ddd.subject.application.command;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

/**
 * Application command for changing a subject's lifecycle status.
 * <p>
 * This serves as a general-purpose command for status transitions
 * that are not covered by the dedicated screen/enroll/complete/withdraw
 * commands. The transition is validated by the Subject aggregate's
 * business methods.
 * </p>
 */
public class ChangeSubjectStatusCommand {

    private final SubjectId subjectId;
    private final SubjectStatus newStatus;
    private final String reason;

    /**
     * Constructs a new ChangeSubjectStatusCommand.
     *
     * @param subjectId the subject whose status to change; must not be null
     * @param newStatus the target status; must not be null
     * @param reason    a textual description of why the status is being changed;
     *                  may be null
     */
    public ChangeSubjectStatusCommand(SubjectId subjectId, SubjectStatus newStatus, String reason) {
        this.subjectId = subjectId;
        this.newStatus = newStatus;
        this.reason = reason;
    }

    public SubjectId getSubjectId() {
        return subjectId;
    }

    public SubjectStatus getNewStatus() {
        return newStatus;
    }

    public String getReason() {
        return reason;
    }
}
