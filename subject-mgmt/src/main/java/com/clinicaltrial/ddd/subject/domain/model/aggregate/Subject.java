package com.clinicaltrial.ddd.subject.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.subject.domain.event.SubjectCompletedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectEnrolledEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectScreenedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectStatusChangedEvent;
import com.clinicaltrial.ddd.subject.domain.event.SubjectWithdrawnEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ScreeningInfo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectFallOffReason;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate Root for Subject (受试者入组与状态管理).
 * <p>
 * Manages the lifecycle of a clinical trial subject through the following
 * state machine transitions:
 * </p>
 *
 * <pre>
 * null → SCREENING → ENROLLED → ACTIVE → COMPLETED
 *                (isOpenScreen=false 时跳过 SCREENING 直接 → ENROLLED)
 *                           ACTIVE → TERMINATED
 *                           ACTIVE/SCREENING → WITHDRAWN
 * </pre>
 *
 * <h3>Transition Rules</h3>
 * <ul>
 *   <li>{@code null → SCREENING}: Requires {@link #screen(ScreeningInfo)}.
 *       Only allowed when the subject has never been screened before.</li>
 *   <li>{@code SCREENING/null → ENROLLED}: Requires {@link #enroll()}.
 *       A subject can be enrolled directly from creation if the project has
 *       open screening disabled ({@code isOpenScreen=false}).</li>
 *   <li>{@code ENROLLED → ACTIVE}: Automatic enrollment completion;
 *       occurs implicitly after enrollment.</li>
 *   <li>{@code ACTIVE → COMPLETED}: Requires {@link #complete()}.
 *       Marks the subject as having finished the trial per protocol.</li>
 *   <li>{@code ACTIVE → TERMINATED}: Requires {@link #terminate(SubjectFallOffReason)}.
 *       Investigator- or sponsor-initiated termination.</li>
 *   <li>{@code SCREENING/ACTIVE → WITHDRAWN}: Requires {@link #withdraw(SubjectFallOffReason)}.
 *       Voluntary withdrawal by the subject.</li>
 * </ul>
 *
 * <p>
 * All state transitions enforce business invariants and throw
 * {@link BusinessRuleViolationException} when a transition is not allowed.
 * Domain events are registered and can be retrieved via {@link #pullDomainEvents()}
 * after persistence.
 * </p>
 *
 * @see SubjectStatus the lifecycle status enum
 */
public class Subject extends AggregateRoot<SubjectId> {

    private SubjectId id;
    private ProjectId projectId;
    private SubjectCode code;
    private SubjectStatus status;
    private Long userId;
    private Long siteId;
    private String blh;
    private String syxh;
    private List<String> groupSubsetIds;
    private ScreeningInfo screeningInfo;
    private SubjectFallOffReason fallOffReason;
    private String remarks;
    private Long trackDownId;
    private Long supervisorId;

    /**
     * Constructs a new Subject for creation via the factory method
     * {@link #createForEnrollment(ProjectId, Long, Long, String, String, List)}.
     */
    private Subject() {
        this.status = null;
        this.groupSubsetIds = new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // Factory methods
    // ---------------------------------------------------------------

    /**
     * Creates a new Subject without an initial status. The caller must
     * subsequently call {@link #screen(ScreeningInfo)} or
     * {@link #enroll()} to set the initial lifecycle state.
     *
     * @param projectId      the project this subject belongs to; must not be null
     * @param userId         the user identity of the subject
     * @param siteId         the site identity
     * @param blh            病历号 (medical record number); may be null
     * @param syxh           试验序号 (trial sequence number); may be null
     * @param groupSubsetIds group/subset identifiers; may be null (treated as empty)
     * @return a new Subject instance
     * @throws IllegalArgumentException if projectId is null
     */
    public static Subject createForEnrollment(ProjectId projectId,
                                               Long userId,
                                               Long siteId,
                                               String blh,
                                               String syxh,
                                               List<String> groupSubsetIds) {
        if (projectId == null) {
            throw new IllegalArgumentException("projectId must not be null");
        }
        Subject subject = new Subject();
        subject.projectId = projectId;
        subject.userId = userId;
        subject.siteId = siteId;
        subject.blh = blh;
        subject.syxh = syxh;
        subject.groupSubsetIds = groupSubsetIds != null
                ? new ArrayList<>(groupSubsetIds)
                : new ArrayList<String>();
        return subject;
    }

    /**
     * Reconstructs an existing Subject from persistent storage (used by repositories).
     * All fields are set directly; no domain events are registered.
     *
     * @param id             the persistent identity
     * @param projectId      the project identity
     * @param code           the subject enrollment code
     * @param status         the current lifecycle status (may be null)
     * @param userId         the user identity
     * @param siteId         the site identity
     * @param blh            病历号
     * @param syxh           试验序号
     * @param groupSubsetIds group/subset identifiers
     * @param screeningInfo  screening evaluation information (may be null)
     * @param fallOffReason  fall-off reason (may be null)
     * @param remarks        general remarks (may be null)
     * @param trackDownId    tracking-down user identity (may be null)
     * @param supervisorId   supervisor identity (may be null)
     * @return a reconstructed Subject
     */
    public static Subject reconstruct(SubjectId id,
                                       ProjectId projectId,
                                       SubjectCode code,
                                       SubjectStatus status,
                                       Long userId,
                                       Long siteId,
                                       String blh,
                                       String syxh,
                                       List<String> groupSubsetIds,
                                       ScreeningInfo screeningInfo,
                                       SubjectFallOffReason fallOffReason,
                                       String remarks,
                                       Long trackDownId,
                                       Long supervisorId) {
        Subject subject = new Subject();
        subject.id = id;
        subject.projectId = projectId;
        subject.code = code;
        subject.status = status;
        subject.userId = userId;
        subject.siteId = siteId;
        subject.blh = blh;
        subject.syxh = syxh;
        subject.groupSubsetIds = groupSubsetIds != null
                ? new ArrayList<>(groupSubsetIds)
                : new ArrayList<String>();
        subject.screeningInfo = screeningInfo;
        subject.fallOffReason = fallOffReason;
        subject.remarks = remarks;
        subject.trackDownId = trackDownId;
        subject.supervisorId = supervisorId;
        return subject;
    }

    // ---------------------------------------------------------------
    // Business methods - State machine transitions
    // ---------------------------------------------------------------

    /**
     * Screens the subject. Sets the status to {@link SubjectStatus#SCREENING}.
     * <p>
     * <strong>Permitted only when current status is {@code null}.</strong>
     * </p>
     *
     * @param info the screening evaluation information; must not be null
     * @throws BusinessRuleViolationException if status is not null (subject already screened)
     * @throws IllegalArgumentException       if info is null
     */
    public void screen(ScreeningInfo info) {
        if (info == null) {
            throw new IllegalArgumentException("ScreeningInfo must not be null");
        }
        if (status != null) {
            throw new BusinessRuleViolationException(
                    "SUBJECT_ALREADY_SCREENED",
                    "Subject " + safeId() + " has already been screened; current status is " + status);
        }
        this.status = SubjectStatus.SCREENING;
        this.screeningInfo = info;

        registerEvent(new SubjectScreenedEvent(
                this.id,
                this.projectId,
                LocalDateTime.now()));
    }

    /**
     * Enrolls the subject into the trial.
     * <p>
     * <strong>Permitted only when current status is {@code null} or
     * {@link SubjectStatus#SCREENING}.</strong>
     * </p>
     * <p>
     * When status is {@code null}, this represents a direct enrollment
     * (skipping screening, used when the project has {@code isOpenScreen=false}).
     * When status is {@code SCREENING}, this completes the screening-to-enrollment
     * transition.
     * </p>
     * <p>
     * After enrollment the status advances to {@link SubjectStatus#ENROLLED}.
     * </p>
     *
     * @throws BusinessRuleViolationException if status is neither null nor SCREENING
     */
    public void enroll() {
        if (!canEnroll()) {
            throw new BusinessRuleViolationException(
                    "SUBJECT_CANNOT_ENROLL",
                    "Subject " + safeId() + " cannot be enrolled from status " + status);
        }
        this.status = SubjectStatus.ENROLLED;

        registerEvent(new SubjectEnrolledEvent(
                this.id,
                this.projectId,
                this.code,
                LocalDateTime.now()));
    }

    /**
     * Marks the subject as having completed the trial.
     * <p>
     * <strong>Permitted only when current status is
     * {@link SubjectStatus#ACTIVE}.</strong>
     * </p>
     * <p>
     * Transition: ACTIVE → COMPLETED.
     * </p>
     *
     * @throws BusinessRuleViolationException if status is not ACTIVE
     */
    public void complete() {
        if (status != SubjectStatus.ACTIVE) {
            throw new BusinessRuleViolationException(
                    "SUBJECT_CANNOT_COMPLETE",
                    "Subject " + safeId() + " can only be completed from ACTIVE status, "
                            + "but current status is " + status);
        }
        this.status = SubjectStatus.COMPLETED;

        registerEvent(new SubjectCompletedEvent(
                this.id,
                LocalDateTime.now()));
    }

    /**
     * Terminates the subject's participation in the trial (sponsor/investigator action).
     * <p>
     * <strong>Permitted only when current status is
     * {@link SubjectStatus#ACTIVE}.</strong>
     * </p>
     * <p>
     * Transition: ACTIVE → TERMINATED.
     * </p>
     *
     * @param reason the reason for termination; must not be null
     * @throws BusinessRuleViolationException if status is not ACTIVE
     * @throws IllegalArgumentException       if reason is null
     */
    public void terminate(SubjectFallOffReason reason) {
        if (reason == null) {
            throw new IllegalArgumentException("SubjectFallOffReason must not be null");
        }
        if (status != SubjectStatus.ACTIVE) {
            throw new BusinessRuleViolationException(
                    "SUBJECT_CANNOT_TERMINATE",
                    "Subject " + safeId() + " can only be terminated from ACTIVE status, "
                            + "but current status is " + status);
        }
        SubjectStatus oldStatus = this.status;
        this.status = SubjectStatus.TERMINATED;
        this.fallOffReason = reason;

        registerEvent(new SubjectStatusChangedEvent(
                this.id,
                oldStatus,
                this.status,
                reason.getReasonDescription(),
                LocalDateTime.now()));
    }

    /**
     * Records the subject's voluntary withdrawal from the trial.
     * <p>
     * <strong>Permitted when current status is
     * {@link SubjectStatus#SCREENING} or {@link SubjectStatus#ACTIVE}.</strong>
     * </p>
     * <p>
     * Transition: SCREENING → WITHDRAWN or ACTIVE → WITHDRAWN.
     * </p>
     *
     * @param reason the reason for withdrawal; must not be null
     * @throws BusinessRuleViolationException if status is not SCREENING or ACTIVE
     * @throws IllegalArgumentException       if reason is null
     */
    public void withdraw(SubjectFallOffReason reason) {
        if (reason == null) {
            throw new IllegalArgumentException("SubjectFallOffReason must not be null");
        }
        if (status != SubjectStatus.SCREENING && status != SubjectStatus.ACTIVE) {
            throw new BusinessRuleViolationException(
                    "SUBJECT_CANNOT_WITHDRAW",
                    "Subject " + safeId() + " can only withdraw from SCREENING or ACTIVE status, "
                            + "but current status is " + status);
        }
        SubjectStatus oldStatus = this.status;
        this.status = SubjectStatus.WITHDRAWN;
        this.fallOffReason = reason;

        registerEvent(new SubjectWithdrawnEvent(
                this.id,
                reason.getReasonDescription(),
                LocalDateTime.now()));
    }

    /**
     * Sets the subject's enrollment code. Typically called by the
     * {@link com.clinicaltrial.ddd.subject.domain.service.SubjectCodeGenerator}
     * during the enrollment flow.
     *
     * @param code the enrollment code to assign; must not be null
     * @throws IllegalStateException if a code has already been assigned
     * @throws IllegalArgumentException if code is null
     */
    public void assignCode(SubjectCode code) {
        if (code == null) {
            throw new IllegalArgumentException("SubjectCode must not be null");
        }
        if (this.code != null) {
            throw new IllegalStateException(
                    "Subject " + safeId() + " already has a code: " + this.code);
        }
        this.code = code;
    }

    /**
     * Transitions the subject from {@link SubjectStatus#ENROLLED} to
     * {@link SubjectStatus#ACTIVE}. Called after enrollment formalities are complete.
     * <p>
     * <strong>Permitted only when current status is ENROLLED.</strong>
     * </p>
     *
     * @throws BusinessRuleViolationException if status is not ENROLLED
     */
    public void activate() {
        if (status != SubjectStatus.ENROLLED) {
            throw new BusinessRuleViolationException(
                    "SUBJECT_CANNOT_ACTIVATE",
                    "Subject " + safeId() + " can only be activated from ENROLLED status, "
                            + "but current status is " + status);
        }
        SubjectStatus oldStatus = this.status;
        this.status = SubjectStatus.ACTIVE;

        registerEvent(new SubjectStatusChangedEvent(
                this.id,
                oldStatus,
                this.status,
                "Subject activated after enrollment",
                LocalDateTime.now()));
    }

    // ---------------------------------------------------------------
    // Query methods
    // ---------------------------------------------------------------

    /**
     * Returns whether the subject is eligible for enrollment.
     * <p>
     * A subject can be enrolled if their current status is {@code null}
     * (not yet in the lifecycle) or {@link SubjectStatus#SCREENING}
     * (currently being screened).
     * </p>
     *
     * @return {@code true} if the subject can be enrolled
     */
    public boolean canEnroll() {
        return status == null || status == SubjectStatus.SCREENING;
    }

    /**
     * Returns whether the subject is currently active in the trial.
     *
     * @return {@code true} if status is ACTIVE
     */
    public boolean isActive() {
        return status == SubjectStatus.ACTIVE;
    }

    /**
     * Returns whether the subject has completed the trial.
     *
     * @return {@code true} if status is COMPLETED
     */
    public boolean isCompleted() {
        return status == SubjectStatus.COMPLETED;
    }

    /**
     * Returns whether the subject has been terminated or withdrawn
     * (i.e., is no longer participating for any reason).
     *
     * @return {@code true} if status is TERMINATED or WITHDRAWN
     */
    public boolean isDiscontinued() {
        return status == SubjectStatus.TERMINATED || status == SubjectStatus.WITHDRAWN;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public SubjectId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public SubjectCode getCode() {
        return code;
    }

    public SubjectStatus getStatus() {
        return status;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public String getBlh() {
        return blh;
    }

    public String getSyxh() {
        return syxh;
    }

    public List<String> getGroupSubsetIds() {
        return groupSubsetIds != null
                ? Collections.unmodifiableList(groupSubsetIds)
                : Collections.<String>emptyList();
    }

    public ScreeningInfo getScreeningInfo() {
        return screeningInfo;
    }

    public SubjectFallOffReason getFallOffReason() {
        return fallOffReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public Long getTrackDownId() {
        return trackDownId;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    // ---------------------------------------------------------------
    // Setters for mutable fields (non-state-machine)
    // ---------------------------------------------------------------

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setTrackDownId(Long trackDownId) {
        this.trackDownId = trackDownId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }

    // ---------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------

    /**
     * Returns a safe representation of the subject identity for error messages,
     * handling the case where the id has not yet been assigned (transient aggregate).
     */
    private String safeId() {
        return id != null ? id.toString() : "(transient)";
    }
}
