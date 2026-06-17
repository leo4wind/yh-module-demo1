package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 保存字段值请求.
 */
public class SaveFieldValueRequest {

    private String fieldCode;
    private String fieldLabel;
    private String fieldValue;
    private String fieldValueText;
    private String dataUnit;
    private String fieldType;
    private String subTableId;
    private Long userId;

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
    public String getSubTableId() { return subTableId; }
    public void setSubTableId(String subTableId) { this.subTableId = subTableId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
