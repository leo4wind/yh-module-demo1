package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFormId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Entity representing a CRF form (CRF表单) within a CRF template.
 * <p>
 * A CRF form is a logical grouping of fields within a Case Report Form template.
 * Forms represent structured data collection units such as "Vital Signs",
 * "Medical History", or "Adverse Events". Each form contains an ordered
 * collection of {@link CrfField} instances.
 * </p>
 *
 * <h3>Ownership</h3>
 * CrfForm is an entity owned by the {@link com.clinicaltrial.ddd.trial.domain.model.aggregate.CrfTemplate}
 * aggregate. It is not shared across aggregates and its identity is unique only
 * within the context of its parent template.
 * </p>
 */
public class CrfForm extends Entity<CrfFormId> {

    private CrfFormId id;
    private String modelName;
    private String refName;
    private String rulesName;
    private List<CrfField> fields;

    /**
     * Default constructor for persistence frameworks.
     */
    protected CrfForm() {
        this.fields = new ArrayList<>();
    }

    /**
     * Private constructor used by the factory method.
     */
    private CrfForm(CrfFormId id, String modelName, String refName, String rulesName) {
        this.id = id;
        this.modelName = modelName;
        this.refName = refName;
        this.rulesName = rulesName;
        this.fields = new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new CrfForm entity.
     *
     * @param id        the unique form identity; must not be null
     * @param modelName the model name for this form (e.g., "VitalSigns"); must not be blank
     * @param refName   the reference name used in data mapping; may be null
     * @param rulesName the name used for validation rules binding; may be null
     * @return a new CrfForm instance
     * @throws IllegalArgumentException if id is null or modelName is blank
     */
    public static CrfForm create(CrfFormId id, String modelName, String refName, String rulesName) {
        if (id == null) {
            throw new IllegalArgumentException("CrfForm id must not be null");
        }
        if (modelName == null || modelName.trim().isEmpty()) {
            throw new IllegalArgumentException("CrfForm modelName must not be blank");
        }

        return new CrfForm(id, modelName.trim(), refName, rulesName);
    }

    // ---------------------------------------------------------------
    // Business methods
    // ---------------------------------------------------------------

    /**
     * Adds a field to this form.
     * <p>
     * The field is appended to the end of the field list. Duplicate fields
     * (by identity) are not allowed.
     * </p>
     *
     * @param field the field to add; must not be null
     * @throws IllegalArgumentException if field is null
     * @throws IllegalStateException    if a field with the same identity already exists
     */
    public void addField(CrfField field) {
        if (field == null) {
            throw new IllegalArgumentException("field must not be null");
        }
        if (fieldExists(field.getId())) {
            throw new IllegalStateException(
                    "Field [id=" + field.getId() + "] already exists in form [id=" + this.id + "]");
        }
        this.fields.add(field);
    }

    /**
     * Removes a field from this form by its identity.
     *
     * @param fieldId the identity of the field to remove; must not be null
     * @return {@code true} if the field was found and removed
     * @throws IllegalArgumentException if fieldId is null
     */
    public boolean removeField(CrfFormId fieldId) {
        if (fieldId == null) {
            throw new IllegalArgumentException("fieldId must not be null");
        }
        return this.fields.removeIf(f -> f.getId().equals(fieldId));
    }

    /**
     * Returns an unmodifiable view of the fields in this form.
     *
     * @return the list of fields (unmodifiable)
     */
    public List<CrfField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    /**
     * Checks whether a field with the given identity already exists in this form.
     *
     * @param fieldId the field identity to check
     * @return {@code true} if a field with that identity exists
     */
    private boolean fieldExists(CrfFieldId fieldId) {
        return fields.stream().anyMatch(f -> f.getId().equals(fieldId));
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public CrfFormId getId() {
        return id;
    }

    public String getModelName() {
        return modelName;
    }

    public String getRefName() {
        return refName;
    }

    public String getRulesName() {
        return rulesName;
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(CrfFormId id) {
        this.id = id;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    public void setRulesName(String rulesName) {
        this.rulesName = rulesName;
    }

    public void setFields(List<CrfField> fields) {
        this.fields = fields != null ? new ArrayList<>(fields) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "CrfForm{id=" + id + ", modelName='" + modelName + "', fieldCount="
                + (fields != null ? fields.size() : 0) + '}';
    }
}
