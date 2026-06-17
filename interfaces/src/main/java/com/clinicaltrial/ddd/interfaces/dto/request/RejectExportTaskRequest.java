package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 审核驳回导出任务请求.
 */
public class RejectExportTaskRequest {

    private String userId;
    private String reason;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
