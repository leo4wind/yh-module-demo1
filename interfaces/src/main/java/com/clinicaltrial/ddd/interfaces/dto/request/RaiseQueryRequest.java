package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 发起质疑请求.
 */
public class RaiseQueryRequest {

    private Long assessmentId;
    private String fieldCode;
    private String subTableId;
    private String fieldType;
    private String question;
    private String originalFieldCode;
    private String originalFieldValue;
    private String originalFieldValueText;
    private Long userId;

    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
    public String getFieldCode() { return fieldCode; }
    public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }
    public String getSubTableId() { return subTableId; }
    public void setSubTableId(String subTableId) { this.subTableId = subTableId; }
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getOriginalFieldCode() { return originalFieldCode; }
    public void setOriginalFieldCode(String originalFieldCode) { this.originalFieldCode = originalFieldCode; }
    public String getOriginalFieldValue() { return originalFieldValue; }
    public void setOriginalFieldValue(String originalFieldValue) { this.originalFieldValue = originalFieldValue; }
    public String getOriginalFieldValueText() { return originalFieldValueText; }
    public void setOriginalFieldValueText(String originalFieldValueText) { this.originalFieldValueText = originalFieldValueText; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
