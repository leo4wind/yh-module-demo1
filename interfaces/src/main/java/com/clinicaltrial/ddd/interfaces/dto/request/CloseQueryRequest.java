package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 关闭质疑请求.
 */
public class CloseQueryRequest {

    private Long userId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
