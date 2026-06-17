package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 稽查评估请求.
 */
public class AuditAssessmentRequest {

    private Long userId;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
