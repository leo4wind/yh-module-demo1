package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldOptionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity representing a single data field within a CRF form (CRF字段).
 * <p>
 * A field captures an individual data point in a Case Report Form, such as
 * a temperature reading, medication name, or lab result. Fields have a type
 * that determines how they are rendered and validated.
 * </p>
 *
 * <h3>Field Types and Options</h3>
 * <ul>
 *   <li>{@link FieldType#SELECT}, {@link FieldType#RADIO}, and
 *       {@link FieldType#CHECKBOX} fields support pre-defined {@link CrfFieldOption options}.</li>
 *   <li>Other field types ignore the options list.</li>
 * </ul>
 *
 * <h3>Completeness Tracking</h3>
 * A field is considered countable for completeness calculation when it is
 * both <strong>not hidden</strong> and <strong>required</strong>
 * ({@link #isCountableForCompleteness()}).
 * </p>
 */
public class CrfField extends Entity<CrfFieldId> {

    private CrfFieldId id;
    private String fieldCode;
    private String fieldLabel;
    private FieldType fieldType;
    private String defaultValue;
    private String dataUnit;
    private boolean required;
    private boolean hidden;
    private Integer sortOrder;
    private List<CrfFieldOption> options;
    private Map<String, Object> validationRules;

    /**
     * Default constructor for persistence frameworks.
     */
    protected CrfField() {
        this.options = new ArrayList<>();
        this.validationRules = new HashMap<>();
    }

    /**
     * Private constructor used by the factory method.
     */
    private CrfField(CrfFieldId id, String fieldCode, String fieldLabel, FieldType fieldType,
                      String defaultValue, String dataUnit, boolean required, boolean hidden,
                      Integer sortOrder) {
        this.id = id;
        this.fieldCode = fieldCode;
        this.fieldLabel = fieldLabel;
        this.fieldType = fieldType;
        this.defaultValue = defaultValue;
        this.dataUnit = dataUnit;
        this.required = required;
        this.hidden = hidden;
        this.sortOrder = sortOrder;
        this.options = new ArrayList<>();
        this.validationRules = new HashMap<>();
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new CrfField entity.
     *
     * @param id           the unique field identity; must not be null
     * @param fieldCode    the field code (e.g., "btbm00"); must not be blank
     * @param fieldLabel   the field display label (e.g., "体温"); must not be blank
     * @param fieldType    the field data type; must not be null
     * @param defaultValue the default value for new records; may be null
     * @param dataUnit     the unit of measurement (e.g., "℃"); may be null
     * @param required     whether the field is mandatory
     * @param hidden       whether the field is hidden from data entry
     * @param sortOrder    the display order within the form; may be null
     * @return a new CrfField instance
     * @throws IllegalArgumentException if id, fieldCode, fieldLabel, or fieldType is null
     */
    public static CrfField create(CrfFieldId id, String fieldCode, String fieldLabel,
                                   FieldType fieldType, String defaultValue, String dataUnit,
                                   boolean required, boolean hidden, Integer sortOrder) {
        if (id == null) {
            throw new IllegalArgumentException("CrfField id must not be null");
        }
        if (fieldCode == null || fieldCode.trim().isEmpty()) {
            throw new IllegalArgumentException("CrfField fieldCode must not be blank");
        }
        if (fieldLabel == null || fieldLabel.trim().isEmpty()) {
            throw new IllegalArgumentException("CrfField fieldLabel must not be blank");
        }
        if (fieldType == null) {
            throw new IllegalArgumentException("CrfField fieldType must not be null");
        }

        return new CrfField(id, fieldCode.trim(), fieldLabel.trim(), fieldType,
                defaultValue, dataUnit, required, hidden, sortOrder);
    }

    // ---------------------------------------------------------------
    // Business methods
    // ---------------------------------------------------------------

    /**
     * Hides this field from data entry.
     * <p>
     * Hidden fields are not displayed in data entry forms and are not counted
     * towards completeness calculations.
     * </p>
     */
    public void hide() {
        this.hidden = true;
    }

    /**
     * Shows this field in data entry (undoes {@link #hide()}).
     */
    public void show() {
        this.hidden = false;
    }

    /**
     * Adds a pre-defined option to this field.
     * <p>
     * Options are applicable only for {@link FieldType#SELECT},
     * {@link FieldType#RADIO}, and {@link FieldType#CHECKBOX} field types.
     * Adding an option to other field types is allowed but has no rendering
     * effect.
     * </p>
     *
     * @param option the option to add; must not be null
     * @throws IllegalArgumentException if option is null
     */
    public void addOption(CrfFieldOption option) {
        if (option == null) {
            throw new IllegalArgumentException("option must not be null");
        }
        this.options.add(option);
    }

    /**
     * Returns whether this field should be counted when calculating form
     * completion completeness.
     * <p>
     * A field is countable when it is <strong>not hidden</strong> and
     * <strong>required</strong>. Hidden or optional fields are excluded
     * from completeness calculations.
     * </p>
     *
     * @return {@code true} if this field contributes to completeness
     */
    public boolean isCountableForCompleteness() {
        return !hidden && required;
    }

    /**
     * Adds a validation rule to this field.
     *
     * @param key   the rule key (e.g., "min", "max", "pattern")
     * @param value the rule configuration value
     */
    public void addValidationRule(String key, Object value) {
        if (key == null) {
            throw new IllegalArgumentException("validation rule key must not be null");
        }
        this.validationRules.put(key, value);
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public CrfFieldId getId() {
        return id;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getDataUnit() {
        return dataUnit;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isHidden() {
        return hidden;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public List<CrfFieldOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public Map<String, Object> getValidationRules() {
        return Collections.unmodifiableMap(validationRules);
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(CrfFieldId id) {
        this.id = id;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setOptions(List<CrfFieldOption> options) {
        this.options = options != null ? new ArrayList<>(options) : new ArrayList<>();
    }

    public void setValidationRules(Map<String, Object> validationRules) {
        this.validationRules = validationRules != null
                ? new HashMap<>(validationRules) : new HashMap<>();
    }

    @Override
    public String toString() {
        return "CrfField{id=" + id + ", code='" + fieldCode + "', label='" + fieldLabel
                + "', type=" + fieldType + '}';
    }
}
