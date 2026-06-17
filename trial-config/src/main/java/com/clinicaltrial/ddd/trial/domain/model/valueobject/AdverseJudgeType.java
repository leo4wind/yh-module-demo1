package com.clinicaltrial.ddd.trial.domain.model.valueobject;

/**
 * Enum representing the judgment type for automatic adverse event detection.
 * <p>
 * Defines how an adverse event rule evaluates CRF data to determine whether
 * an adverse event should be triggered.
 * </p>
 *
 * <ul>
 *   <li>{@link #FIELD_VALUE} (字段值判断) — Rule matches based on a specific
 *       CRF field value (e.g., field code = "AE001", value = "YES").</li>
 *   <li>{@link #LOGIC_EXPRESSION} (逻辑表达式判断) — Rule matches based on a
 *       complex logical expression involving multiple fields.</li>
 * </ul>
 */
public enum AdverseJudgeType {

    FIELD_VALUE("字段值判断"),
    LOGIC_EXPRESSION("逻辑表达式判断");

    private final String displayName;

    AdverseJudgeType(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the Chinese display name for this judge type.
     *
     * @return the localized type label
     */
    public String getDisplayName() {
        return displayName;
    }
}
