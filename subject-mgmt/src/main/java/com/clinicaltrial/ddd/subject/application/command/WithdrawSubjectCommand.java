package com.clinicaltrial.ddd.subject.application.command;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

/**
 * Application command for withdrawing a subject from the trial.
 * <p>
 * Withdrawal is a voluntary action initiated by the subject and is
 * only permitted when the subject is in {@code SCREENING} or
 * {@code ACTIVE} status.
 * </p>
 */
public class WithdrawSubjectCommand {

    private final SubjectId subjectId;
    private final String reasonCode;
    private final String reasonDescription;

    /**
     * Constructs a new WithdrawSubjectCommand.
     *
     * @param subjectId        the subject to withdraw; must not be null
     * @param reasonCode       a short code identifying the reason (e.g. "WITHDRAWAL_OF_CONSENT")
     * @param reasonDescription a human-readable description of the withdrawal reason
     */
    public WithdrawSubjectCommand(SubjectId subjectId, String reasonCode, String reasonDescription) {
        this.subjectId = subjectId;
        this.reasonCode = reasonCode;
        this.reasonDescription = reasonDescription;
    }

    public SubjectId getSubjectId() {
        return subjectId;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }
}
