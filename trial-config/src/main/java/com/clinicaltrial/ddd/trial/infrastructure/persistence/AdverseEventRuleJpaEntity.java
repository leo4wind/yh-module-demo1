package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseJudgeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.AdverseEventRule} domain entity.
 */
@Entity
@Table(name = "rd_project_ae_rule")
public class AdverseEventRuleJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    @Column(name = "crf_id", nullable = false)
    private Long crfId;

    @Column(name = "crf_version_id")
    private Long crfVersionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "judge_type", nullable = false, length = 30)
    private AdverseJudgeType judgeType;

    @Column(name = "field_code", length = 100)
    private String fieldCode;

    @Column(name = "field_name", length = 200)
    private String fieldName;

    @Column(name = "value_code", length = 100)
    private String valueCode;

    @Column(name = "value_name", length = 200)
    private String valueName;

    public AdverseEventRuleJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public Long getCrfId() {
        return crfId;
    }

    public void setCrfId(Long crfId) {
        this.crfId = crfId;
    }

    public Long getCrfVersionId() {
        return crfVersionId;
    }

    public void setCrfVersionId(Long crfVersionId) {
        this.crfVersionId = crfVersionId;
    }

    public AdverseJudgeType getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(AdverseJudgeType judgeType) {
        this.judgeType = judgeType;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public String getValueName() {
        return valueName;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }
}
