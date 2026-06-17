package com.clinicaltrial.ddd.subject.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.subject.application.command.ChangeSubjectStatusCommand;
import com.clinicaltrial.ddd.subject.application.command.EnrollSubjectCommand;
import com.clinicaltrial.ddd.subject.application.command.ScreenSubjectCommand;
import com.clinicaltrial.ddd.subject.application.command.WithdrawSubjectCommand;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectFallOffReason;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;
import com.clinicaltrial.ddd.subject.domain.repository.SubjectRepository;
import com.clinicaltrial.ddd.subject.domain.service.SubjectCodeGenerator;
import com.clinicaltrial.ddd.subject.domain.service.SubjectEnrollmentDomainService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

/**
 * Application service for the Subject Management bounded context.
 * <p>
 * Orchestrates use cases by coordinating between the domain layer
 * (aggregates, domain services, repository) and infrastructure
 * (event bus). The application service does <strong>not</strong>
 * contain business logic -- all business rules and validations are
 * enforced by the Subject aggregate and domain services.
 * </p>
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Receive application commands and convert them to domain operations</li>
 *   <li>Load aggregates from the repository</li>
 *   <li>Invoke domain methods on aggregates</li>
 *   <li>Persist changes via the repository</li>
 *   <li>Publish domain events via the EventBus</li>
 *   <li>Manage transactional boundaries</li>
 * </ul>
 */
@Service
public class SubjectApplicationService {

    private final SubjectRepository subjectRepository;
    private final SubjectEnrollmentDomainService enrollmentDomainService;
    private final SubjectCodeGenerator codeGenerator;
    private final EventBus eventBus;

    /**
     * Constructs the application service with its required dependencies.
     *
     * @param subjectRepository         the repository for Subject aggregates
     * @param enrollmentDomainService   the domain service coordinating enrollment flow
     * @param codeGenerator             the domain service for generating subject codes
     * @param eventBus                  the infrastructure event bus for publishing domain events
     */
    public SubjectApplicationService(SubjectRepository subjectRepository,
                                      SubjectEnrollmentDomainService enrollmentDomainService,
                                      SubjectCodeGenerator codeGenerator,
                                      EventBus eventBus) {
        this.subjectRepository = subjectRepository;
        this.enrollmentDomainService = enrollmentDomainService;
        this.codeGenerator = codeGenerator;
        this.eventBus = eventBus;
    }

    /**
     * Screens a new subject.
     * <p>
     * Creates a new Subject, applies the screening information, persists the
     * aggregate, and publishes the resulting domain events.
     * </p>
     *
     * @param command the screening command containing subject and screening data
     * @return the saved subject with its generated identity
     * @throws IllegalArgumentException         if command is null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                          if the subject cannot be screened
     */
    public Subject screenSubject(ScreenSubjectCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        // Create a new subject aggregate
        Subject subject = Subject.createForEnrollment(
                command.getProjectId(),
                command.getUserId(),
                command.getSiteId(),
                command.getBlh(),
                command.getSyxh(),
                command.getName(),
                command.getGender(),
                command.getAge(),
                Collections.<String>emptyList());
        subject.assignId(generateSubjectId());

        // Build screening info value object
        ScreeningInfo screeningInfo = new ScreeningInfo(
                command.getScreeningDate(),
                command.getScreeningResult(),
                command.getRemarks());

        // Apply the screen transition (domain method validates invariants)
        subject.screen(screeningInfo);

        // Persist the aggregate
        Subject savedSubject = subjectRepository.save(subject);

        // Publish all pending domain events
        eventBus.publishAll(subject);

        return savedSubject;
    }

    /**
     * Enrolls a subject into the trial.
     * <p>
     * Two scenarios are supported:
     * <ol>
     *   <li><strong>Screen-then-enroll:</strong> If the command contains a
     *       subjectId, the existing subject (in SCREENING status) is loaded
     *       and enrolled through the enrollment domain service with
     *       {@code isOpenScreen=true}.</li>
     *   <li><strong>Direct enrollment:</strong> If the command has no subjectId,
     *       a new subject is created and enrolled directly through the
     *       enrollment domain service with {@code isOpenScreen=false}.</li>
     * </ol>
     * </p>
     *
     * @param command the enrollment command
     * @return the enrolled subject
     * @throws IllegalArgumentException         if command is null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                          if the enrollment transition is not permitted
     */
    public Subject enrollSubject(EnrollSubjectCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        Subject subject;

        if (command.getSubjectId() != null) {
            // Scenario 1: A previously screened subject is being enrolled
            subject = subjectRepository.getById(command.getSubjectId());
            enrollmentDomainService.enrollSubject(subject, command.getProjectId(), true);
        } else {
            // Scenario 2: Direct enrollment (isOpenScreen = false)
            SubjectCode code = codeGenerator.generateNextCode(command.getProjectId());

            subject = Subject.createForEnrollment(
                    command.getProjectId(),
                    command.getUserId(),
                    command.getSiteId(),
                    command.getBlh(),
                    command.getSyxh(),
                    command.getName(),
                    command.getGender(),
                    command.getAge(),
                    command.getGroupSubsetIds());
            subject.assignId(generateSubjectId());

            subject.assignCode(code);

            // Direct enrollment: status null → ENROLLED
            subject.enroll();
        }

        Subject savedSubject = subjectRepository.save(subject);
        eventBus.publishAll(subject);

        return savedSubject;
    }

    /**
     * Withdraws a subject from the trial (voluntary action by the subject).
     * <p>
     * The subject must currently be in {@code SCREENING} or {@code ACTIVE}
     * status. The withdrawal reason is recorded as a
     * {@link SubjectFallOffReason} value object.
     * </p>
     *
     * @param command the withdrawal command containing subject identity and reason
     * @return the updated subject
     * @throws IllegalArgumentException         if command is null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                          if the subject cannot be withdrawn
     */
    public Subject withdrawSubject(WithdrawSubjectCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        Subject subject = subjectRepository.getById(command.getSubjectId());

        SubjectFallOffReason fallOffReason = new SubjectFallOffReason(
                command.getReasonCode(),
                command.getReasonDescription(),
                LocalDate.now());

        subject.withdraw(fallOffReason);

        Subject savedSubject = subjectRepository.save(subject);
        eventBus.publishAll(subject);

        return savedSubject;
    }

    /**
     * Changes a subject's lifecycle status.
     * <p>
     * This is a general-purpose status change operation that delegates to the
     * appropriate domain method based on the requested target status:
     * <ul>
     *   <li>{@link SubjectStatus#COMPLETED} → calls {@link Subject#complete()}</li>
     *   <li>{@link SubjectStatus#TERMINATED} → calls {@link Subject#terminate(SubjectFallOffReason)}</li>
     *   <li>{@link SubjectStatus#WITHDRAWN} → calls {@link Subject#withdraw(SubjectFallOffReason)}</li>
     *   <li>{@link SubjectStatus#ACTIVE} → calls {@link Subject#activate()}</li>
     *   <li>Other statuses → throws {@link UnsupportedOperationException}</li>
     * </ul>
     * </p>
     *
     * @param command the status change command
     * @return the updated subject
     * @throws IllegalArgumentException         if command is null
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *                                          if the requested transition is not permitted
     * @throws UnsupportedOperationException    if the requested status cannot be
     *                                          set via this generic command
     */
    public Subject changeStatus(ChangeSubjectStatusCommand command) {
        if (command == null) {
            throw new IllegalArgumentException("command must not be null");
        }

        Subject subject = subjectRepository.getById(command.getSubjectId());

        switch (command.getNewStatus()) {
            case COMPLETED:
                subject.complete();
                break;

            case TERMINATED:
                SubjectFallOffReason terminationReason = new SubjectFallOffReason(
                        "TERMINATED",
                        command.getReason() != null ? command.getReason() : "Terminated by investigator",
                        LocalDate.now());
                subject.terminate(terminationReason);
                break;

            case WITHDRAWN:
                SubjectFallOffReason withdrawalReason = new SubjectFallOffReason(
                        "WITHDRAWN",
                        command.getReason() != null ? command.getReason() : "Voluntary withdrawal",
                        LocalDate.now());
                subject.withdraw(withdrawalReason);
                break;

            case ACTIVE:
                subject.activate();
                break;

            default:
                throw new UnsupportedOperationException(
                        "Cannot change subject status to " + command.getNewStatus()
                                + " via generic command. Use the dedicated command instead.");
        }

        Subject savedSubject = subjectRepository.save(subject);
        eventBus.publishAll(subject);

        return savedSubject;
    }

    private SubjectId generateSubjectId() {
        return new SubjectId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE);
    }
}
