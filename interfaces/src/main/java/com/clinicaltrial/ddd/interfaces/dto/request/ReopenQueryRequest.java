package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 重新打开质疑请求.
 */
public class ReopenQueryRequest {

    private String reason;
    private Long userId;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
