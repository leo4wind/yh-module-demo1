package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.CrfField} domain entity.
 */
@Entity
@Table(name = "rd_crf_field")
public class CrfFieldJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "field_code", nullable = false, length = 100)
    private String fieldCode;

    @Column(name = "field_label", nullable = false, length = 200)
    private String fieldLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 20)
    private FieldType fieldType;

    @Column(name = "default_value", length = 500)
    private String defaultValue;

    @Column(name = "data_unit", length = 50)
    private String dataUnit;

    @Column(name = "required")
    private boolean required;

    @Column(name = "hidden")
    private boolean hidden;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Lob
    @Column(name = "validation_rules")
    private String validationRules;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "field_id")
    private List<CrfFieldOptionJpaEntity> options = new ArrayList<>();

    public CrfFieldJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldLabel() {
        return fieldLabel;
    }

    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDataUnit() {
        return dataUnit;
    }

    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(String validationRules) {
        this.validationRules = validationRules;
    }

    public List<CrfFieldOptionJpaEntity> getOptions() {
        return options;
    }

    public void setOptions(List<CrfFieldOptionJpaEntity> options) {
        this.options = options;
    }
}
