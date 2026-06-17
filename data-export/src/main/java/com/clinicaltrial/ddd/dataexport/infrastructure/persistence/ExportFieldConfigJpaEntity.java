package com.clinicaltrial.ddd.dataexport.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the ExportFieldConfig domain entity.
 * Maps to the rd_export_field_config table.
 */
@Entity
@Table(name = "rd_export_field_config")
public class ExportFieldConfigJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "field_code", length = 100)
    private String fieldCode;

    @Column(name = "field_label", length = 200)
    private String fieldLabel;

    @Column(name = "source_type", length = 100)
    private String sourceType;

    @Column(name = "crf_version_id")
    private String crfVersionId;

    public ExportFieldConfigJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
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

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCrfVersionId() {
        return crfVersionId;
    }

    public void setCrfVersionId(String crfVersionId) {
        this.crfVersionId = crfVersionId;
    }
}
