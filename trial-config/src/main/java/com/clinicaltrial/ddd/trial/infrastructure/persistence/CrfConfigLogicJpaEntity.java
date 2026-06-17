package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity for CRF display logic configuration (显示逻辑).
 * <p>
 * Stores conditional display rules for CRF fields based on values of other fields.
 * </p>
 */
@Entity
@Table(name = "rd_crf_config_logic")
public class CrfConfigLogicJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "crf_id", nullable = false)
    private Long crfId;

    @Column(name = "field_code", nullable = false, length = 100)
    private String fieldCode;

    @Column(name = "condition_field_code", length = 100)
    private String conditionFieldCode;

    @Column(name = "condition_value", length = 200)
    private String conditionValue;

    @Column(name = "action", length = 20)
    private String action;

    @Column(name = "sort_order")
    private Integer sortOrder;

    public CrfConfigLogicJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCrfId() {
        return crfId;
    }

    public void setCrfId(Long crfId) {
        this.crfId = crfId;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getConditionFieldCode() {
        return conditionFieldCode;
    }

    public void setConditionFieldCode(String conditionFieldCode) {
        this.conditionFieldCode = conditionFieldCode;
    }

    public String getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(String conditionValue) {
        this.conditionValue = conditionValue;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
