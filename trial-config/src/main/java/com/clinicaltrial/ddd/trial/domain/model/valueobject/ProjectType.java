package com.clinicaltrial.ddd.trial.domain.model.valueobject;

/**
 * Enum representing the type of a clinical trial project.
 * <p>
 * Clinical trials are broadly classified into two types based on their design:
 * </p>
 *
 * <ul>
 *   <li>{@link #INTERVENTIONAL} (干预性研究) — Subjects receive specific
 *       interventions (drugs, devices, procedures) according to a protocol.</li>
 *   <li>{@link #OBSERVATIONAL} (观察性研究) — Subjects are observed without
 *       any assigned intervention; data is collected per routine practice.</li>
 * </ul>
 */
public enum ProjectType {

    INTERVENTIONAL("干预性研究"),
    OBSERVATIONAL("观察性研究");

    private final String displayName;

    ProjectType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the Chinese display name for this project type.
     *
     * @return the localized type label
     */
    public String getDisplayName() {
        return displayName;
    }
}
