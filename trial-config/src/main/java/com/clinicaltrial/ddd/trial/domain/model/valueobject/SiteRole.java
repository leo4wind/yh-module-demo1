package com.clinicaltrial.ddd.trial.domain.model.valueobject;

/**
 * Enum representing roles that personnel can hold at a clinical trial site.
 * <p>
 * Each role has specific responsibilities and access permissions within the
 * trial management system:
 * </p>
 *
 * <ul>
 *   <li>{@link #PI} (主要研究者) — Principal Investigator: oversees the entire
 *       trial at the site.</li>
 *   <li>{@link #SUB_INVESTIGATOR} (助理研究者) — Sub-investigator: assists the
 *       PI in study-related medical decisions.</li>
 *   <li>{@link #CRC} (临床研究协调员) — Clinical Research Coordinator: manages
 *       day-to-day trial operations at the site.</li>
 *   <li>{@link #CRA} (临床监查员) — Clinical Research Associate: monitors trial
 *       data and compliance (sponsor representative).</li>
 *   <li>{@link #AUDITOR} (稽查员) — Auditor: conducts independent audits of
 *       trial data and processes.</li>
 * </ul>
 */
public enum SiteRole {

    PI("主要研究者"),
    SUB_INVESTIGATOR("助理研究者"),
    CRC("临床研究协调员"),
    CRA("临床监查员"),
    AUDITOR("稽查员");

    private final String displayName;

    SiteRole(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the Chinese display name for this role.
     *
     * @return the localized role label
     */
    public String getDisplayName() {
        return displayName;
    }
}
