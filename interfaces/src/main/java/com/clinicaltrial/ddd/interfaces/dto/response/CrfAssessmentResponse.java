package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.Date;
import java.util.List;

/**
 * CRF评估详情响应.
 */
public class CrfAssessmentResponse {

    private Long id;
    private Long subjectStageId;
    private Long stageId;
    private Long crfTemplateId;
    private Long crfVersionId;
    private String status;
    private Double completeness;
    private String monitoringStatus;
    private List<FieldValueVo> fieldValues;
    private Long auditUserId;
    private Date auditTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubjectStageId() { return subjectStageId; }
    public void setSubjectStageId(Long subjectStageId) { this.subjectStageId = subjectStageId; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public Long getCrfTemplateId() { return crfTemplateId; }
    public void setCrfTemplateId(Long crfTemplateId) { this.crfTemplateId = crfTemplateId; }
    public Long getCrfVersionId() { return crfVersionId; }
    public void setCrfVersionId(Long crfVersionId) { this.crfVersionId = crfVersionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getCompleteness() { return completeness; }
    public void setCompleteness(Double completeness) { this.completeness = completeness; }
    public String getMonitoringStatus() { return monitoringStatus; }
    public void setMonitoringStatus(String monitoringStatus) { this.monitoringStatus = monitoringStatus; }
    public List<FieldValueVo> getFieldValues() { return fieldValues; }
    public void setFieldValues(List<FieldValueVo> fieldValues) { this.fieldValues = fieldValues; }
    public Long getAuditUserId() { return auditUserId; }
    public void setAuditUserId(Long auditUserId) { this.auditUserId = auditUserId; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }

    public static class FieldValueVo {
        private String fieldCode;
        private String fieldLabel;
        private String fieldValue;
        private String fieldValueText;
        private String dataUnit;
        private String fieldType;

        public String getFieldCode() { return fieldCode; }
        public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }
        public String getFieldLabel() { return fieldLabel; }
        public void setFieldLabel(String fieldLabel) { this.fieldLabel = fieldLabel; }
        public String getFieldValue() { return fieldValue; }
        public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }
        public String getFieldValueText() { return fieldValueText; }
        public void setFieldValueText(String fieldValueText) { this.fieldValueText = fieldValueText; }
        public String getDataUnit() { return dataUnit; }
        public void setDataUnit(String dataUnit) { this.dataUnit = dataUnit; }
        public String getFieldType() { return fieldType; }
        public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    }
}
