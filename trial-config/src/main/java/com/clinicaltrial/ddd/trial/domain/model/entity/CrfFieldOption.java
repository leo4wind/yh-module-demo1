package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfFieldOptionId;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity representing a pre-defined option for a CRF field (CRF字段选项).
 * <p>
 * Options are used for {@link com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType#SELECT},
 * {@link com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType#RADIO}, and
 * {@link com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType#CHECKBOX} field types.
 * Each option has a display label, a stored value, a sort order for display, and an
 * optional score that can be used for scoring or calculation purposes.
 * </p>
 *
 * <h3>Ownership</h3>
 * CrfFieldOption is an entity owned by the {@link CrfField} entity. Its identity
 * is unique only within the context of its parent field.
 * </p>
 */
public class CrfFieldOption extends Entity<CrfFieldOptionId> {

    private CrfFieldOptionId id;
    private String optionLabel;
    private String optionValue;
    private Integer sortOrder;
    private BigDecimal score;

    /**
     * Default constructor for persistence frameworks.
     */
    protected CrfFieldOption() {
    }

    /**
     * Private constructor used by the factory method.
     */
    private CrfFieldOption(CrfFieldOptionId id, String optionLabel, String optionValue,
                            Integer sortOrder, BigDecimal score) {
        this.id = id;
        this.optionLabel = optionLabel;
        this.optionValue = optionValue;
        this.sortOrder = sortOrder;
        this.score = score;
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new CrfFieldOption entity.
     *
     * @param id           the unique option identity; must not be null
     * @param optionLabel  the display label for the option (e.g., "Yes"); must not be blank
     * @param optionValue  the stored value for the option (e.g., "Y"); must not be blank
     * @param sortOrder    the display order; may be null
     * @param score        the score associated with this option; may be null
     * @return a new CrfFieldOption instance
     * @throws IllegalArgumentException if id, optionLabel, or optionValue is null
     */
    public static CrfFieldOption create(CrfFieldOptionId id, String optionLabel,
                                         String optionValue, Integer sortOrder, BigDecimal score) {
        if (id == null) {
            throw new IllegalArgumentException("CrfFieldOption id must not be null");
        }
        if (optionLabel == null || optionLabel.trim().isEmpty()) {
            throw new IllegalArgumentException("CrfFieldOption optionLabel must not be blank");
        }
        if (optionValue == null || optionValue.trim().isEmpty()) {
            throw new IllegalArgumentException("CrfFieldOption optionValue must not be blank");
        }

        return new CrfFieldOption(id, optionLabel.trim(), optionValue.trim(), sortOrder, score);
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public CrfFieldOptionId getId() {
        return id;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public BigDecimal getScore() {
        return score;
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(CrfFieldOptionId id) {
        this.id = id;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "CrfFieldOption{id=" + id + ", label='" + optionLabel
                + "', value='" + optionValue + "'}";
    }
}
