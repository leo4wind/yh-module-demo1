package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 审核通过导出任务请求.
 */
public class ApproveExportTaskRequest {

    private String userId;
    private String message;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
