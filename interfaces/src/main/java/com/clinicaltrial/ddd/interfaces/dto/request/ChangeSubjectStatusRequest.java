package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 修改受试者状态请求.
 */
public class ChangeSubjectStatusRequest {

    private String newStatus;  // SubjectStatus name
    private String reason;

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
