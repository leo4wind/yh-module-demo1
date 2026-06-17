package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 回应质疑请求.
 */
public class RespondQueryRequest {

    private String response;
    private String updateType;   // CLARIFY_ONLY / MODIFY_VALUE
    private String newFieldValue;
    private String newFieldValueText;
    private Long userId;

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    public String getUpdateType() { return updateType; }
    public void setUpdateType(String updateType) { this.updateType = updateType; }
    public String getNewFieldValue() { return newFieldValue; }
    public void setNewFieldValue(String newFieldValue) { this.newFieldValue = newFieldValue; }
    public String getNewFieldValueText() { return newFieldValueText; }
    public void setNewFieldValueText(String newFieldValueText) { this.newFieldValueText = newFieldValueText; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
