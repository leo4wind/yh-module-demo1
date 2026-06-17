package com.clinicaltrial.ddd.subject.domain.model.valueobject;

/**
 * Enum representing the lifecycle status of a Subject in a clinical trial.
 * <p>
 * <strong>State machine:</strong>
 * <pre>
 * null → SCREENING → ENROLLED → ACTIVE → COMPLETED
 *                (isOpenScreen=false 时跳过 SCREENING 直接 → ENROLLED)
 *                           ACTIVE → TERMINATED
 *                           ACTIVE/SCREENING → WITHDRAWN
 * </pre>
 * </p>
 *
 * <ul>
 *   <li>{@link #SCREENING} — Subject is undergoing screening evaluation.</li>
 *   <li>{@link #ENROLLED} — Subject has been enrolled (screening passed or skipped).</li>
 *   <li>{@link #ACTIVE} — Subject is actively participating in the trial.</li>
 *   <li>{@link #COMPLETED} — Subject has completed the trial per protocol.</li>
 *   <li>{@link #TERMINATED} — Subject participation was terminated by the sponsor/investigator.</li>
 *   <li>{@link #WITHDRAWN} — Subject voluntarily withdrew from the trial.</li>
 * </ul>
 */
public enum SubjectStatus {

    SCREENING,
    ENROLLED,
    ACTIVE,
    COMPLETED,
    TERMINATED,
    WITHDRAWN
}
