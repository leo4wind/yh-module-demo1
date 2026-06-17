package com.clinicaltrial.ddd.trial.domain.model.valueobject;

/**
 * Enum representing the lifecycle status of a clinical trial project.
 * <p>
 * <strong>State machine:</strong>
 * <pre>
 * DRAFT → ACTIVE → CLOSED
 * </pre>
 * </p>
 *
 * <ul>
 *   <li>{@link #DRAFT} (配置中) — Project is being configured; trial configuration
 *       changes are allowed. New projects start in this state.</li>
 *   <li>{@link #ACTIVE} (进行中) — Project is active; subject enrollment and data
 *       collection are in progress. Stage/CRF configuration is locked.</li>
 *   <li>{@link #CLOSED} (已结束) — Project has been closed; no further data
 *       collection or modifications are permitted.</li>
 * </ul>
 */
public enum ProjectStatus {

    DRAFT("配置中"),
    ACTIVE("进行中"),
    CLOSED("已结束");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the Chinese display name for this status.
     *
     * @return the localized status label
     */
    public String getDisplayName() {
        return displayName;
    }
}
