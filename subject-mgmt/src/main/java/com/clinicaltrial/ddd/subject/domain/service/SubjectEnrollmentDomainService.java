package com.clinicaltrial.ddd.subject.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

import java.util.List;

/**
 * Domain service that coordinates the screening-to-enrollment flow for a subject.
 * <p>
 * This service encapsulates the decision logic that depends on project-level
 * configuration (specifically the {@code isOpenScreen} flag): whether a subject
 * must first pass through the SCREENING state before being enrolled, or whether
 * they can be enrolled directly.
 * </p>
 * <p>
 * Usage:
 * <ol>
 *   <li>If {@code isOpenScreen} is {@code true}: the subject must be created,
 *       screened ({@link Subject#screen(ScreeningInfo)}), and then enrolled.</li>
 *   <li>If {@code isOpenScreen} is {@code false}: the subject can be created
 *       and enrolled directly, skipping the screening step.</li>
 * </ol>
 * </p>
 */
@Service
public class SubjectEnrollmentDomainService {

    private final SubjectCodeGenerator codeGenerator;

    /**
     * Constructs the service with the required code generator dependency.
     *
     * @param codeGenerator the domain service for generating subject codes
     */
    public SubjectEnrollmentDomainService(SubjectCodeGenerator codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    /**
     * Enrolls a subject, handling the screening decision based on the project's
     * open-screen configuration.
     * <p>
     * <strong>Flow when {@code isOpenScreen == true}:</strong>
     * <ol>
     *   <li>The subject must already have been screened (status {@code SCREENING}).</li>
     *   <li>A subject code is generated and assigned.</li>
     *   <li>{@link Subject#enroll()} is called to transition to {@code ENROLLED}.</li>
     * </ol>
     *
     * <strong>Flow when {@code isOpenScreen == false}:</strong>
     * <ol>
     *   <li>The subject must be newly created (status {@code null}).</li>
     *   <li>A subject code is generated and assigned.</li>
     *   <li>{@link Subject#enroll()} is called to transition directly to {@code ENROLLED}.</li>
     * </ol>
     * </p>
     *
     * @param subject        the subject to enroll; must not be null
     * @param projectId      the project identity
     * @param isOpenScreen   whether the project requires a screening step
     * @throws BusinessRuleViolationException if the subject's current status is
     *                                        incompatible with the chosen flow
     * @throws IllegalArgumentException       if subject or projectId is null
     */
    public void enrollSubject(Subject subject, ProjectId projectId, boolean isOpenScreen) {
        if (subject == null) {
            throw new IllegalArgumentException("subject must not be null");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("projectId must not be null");
        }

        if (isOpenScreen) {
            // Open screening: must be in SCREENING status before enrollment
            if (subject.getStatus() != SubjectStatus.SCREENING) {
                throw new BusinessRuleViolationException(
                        "SUBJECT_NOT_SCREENED",
                        "Subject " + safeId(subject) + " must be in SCREENING status to enroll "
                                + "when open screen is enabled; current status is " + subject.getStatus());
            }
        } else {
            // Closed screening: must be newly created (null status)
            if (subject.getStatus() != null) {
                throw new BusinessRuleViolationException(
                        "SUBJECT_ALREADY_IN_LIFECYCLE",
                        "Subject " + safeId(subject) + " cannot be directly enrolled "
                                + "because it is already in status " + subject.getStatus()
                                + "; direct enrollment requires null status");
            }
        }

        // Generate and assign the subject code
        if (subject.getCode() == null) {
            subject.assignCode(codeGenerator.generateNextCode(projectId));
        }

        // Perform the enrollment transition
        subject.enroll();
    }

    /**
     * Returns a safe identifier for the subject for use in error messages.
     */
    private String safeId(Subject subject) {
        if (subject.getId() != null) {
            return subject.getId().toString();
        }
        return "(transient)";
    }
}
