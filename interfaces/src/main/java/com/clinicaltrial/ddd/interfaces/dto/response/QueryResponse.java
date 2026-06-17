package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.Date;

/**
 * 质疑响应.
 */
public class QueryResponse {

    private Long id;
    private Long assessmentId;
    private String fieldCode;
    private String subTableId;
    private String fieldType;
    private String status;
    private String type;
    private String question;
    private String response;
    private String updateType;
    private Long createUserId;
    private Long updateUserId;
    private Date createTime;
    private Date updateTime;
    private String originalFieldValue;
    private String originalFieldValueText;
    private String currentFieldValue;
    private String currentFieldValueText;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
    public String getFieldCode() { return fieldCode; }
    public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }
    public String getSubTableId() { return subTableId; }
    public void setSubTableId(String subTableId) { this.subTableId = subTableId; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public String getUpdateType() { return updateType; }
    public void setUpdateType(String updateType) { this.updateType = updateType; }
    public Long getCreateUserId() { return createUserId; }
    public void setCreateUserId(Long createUserId) { this.createUserId = createUserId; }
    public Long getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(Long updateUserId) { this.updateUserId = updateUserId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getOriginalFieldValue() { return originalFieldValue; }
    public void setOriginalFieldValue(String originalFieldValue) { this.originalFieldValue = originalFieldValue; }
    public String getOriginalFieldValueText() { return originalFieldValueText; }
    public void setOriginalFieldValueText(String originalFieldValueText) { this.originalFieldValueText = originalFieldValueText; }
    public String getCurrentFieldValue() { return currentFieldValue; }
    public void setCurrentFieldValue(String currentFieldValue) { this.currentFieldValue = currentFieldValue; }
    public String getCurrentFieldValueText() { return currentFieldValueText; }
    public void setCurrentFieldValueText(String currentFieldValueText) { this.currentFieldValueText = currentFieldValueText; }
}
