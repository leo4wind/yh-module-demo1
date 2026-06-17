package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue} domain entity.
 */
@Entity
@Table(name = "rd_crf_field_value")
public class CrfFieldValueJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;

    @Column(name = "field_code", nullable = false, length = 100)
    private String fieldCode;

    @Column(name = "field_label", length = 200)
    private String fieldLabel;

    @Column(name = "field_value", length = 500)
    private String fieldValue;

    @Column(name = "field_value_text", length = 500)
    private String fieldValueText;

    @Column(name = "data_unit", length = 50)
    private String dataUnit;

    @Column(name = "field_type", length = 50)
    private String fieldType;

    @Column(name = "sub_table_id", length = 100)
    private String subTableId;

    @Column(name = "sort_number")
    private Integer sortNumber;

    public CrfFieldValueJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
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

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public String getFieldValueText() {
        return fieldValueText;
    }

    public void setFieldValueText(String fieldValueText) {
        this.fieldValueText = fieldValueText;
    }

    public String getDataUnit() {
        return dataUnit;
    }

    public void setDataUnit(String dataUnit) {
        this.dataUnit = dataUnit;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getSubTableId() {
        return subTableId;
    }

    public void setSubTableId(String subTableId) {
        this.subTableId = subTableId;
    }

    public Integer getSortNumber() {
        return sortNumber;
    }

    public void setSortNumber(Integer sortNumber) {
        this.sortNumber = sortNumber;
    }
}
