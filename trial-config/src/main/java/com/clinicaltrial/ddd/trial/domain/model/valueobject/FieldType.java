package com.clinicaltrial.ddd.trial.domain.model.valueobject;

/**
 * Enum representing the data type of a CRF field.
 * <p>
 * Determines how a field is rendered in data entry forms and how its value
 * is validated and stored.
 * </p>
 *
 * <ul>
 *   <li>{@link #TEXT} — Single-line text input.</li>
 *   <li>{@link #TEXTAREA} — Multi-line text input.</li>
 *   <li>{@link #NUMBER} — Numeric input (integer or decimal).</li>
 *   <li>{@link #DATE} — Date picker input.</li>
 *   <li>{@link #SELECT} — Drop-down single-select list.</li>
 *   <li>{@link #RADIO} — Radio button group (single selection).</li>
 *   <li>{@link #CHECKBOX} — Checkbox group (multiple selection).</li>
 *   <li>{@link #FILE} — File upload field.</li>
 * </ul>
 */
public enum FieldType {

    TEXT,
    TEXTAREA,
    NUMBER,
    DATE,
    SELECT,
    RADIO,
    CHECKBOX,
    FILE
}
