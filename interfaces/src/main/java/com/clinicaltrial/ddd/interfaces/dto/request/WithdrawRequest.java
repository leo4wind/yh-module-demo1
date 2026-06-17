package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 脱落受试者请求.
 */
public class WithdrawRequest {

    private String reasonCode;
    private String reasonDescription;

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }
    public String getReasonDescription() { return reasonDescription; }
    public void setReasonDescription(String reasonDescription) { this.reasonDescription = reasonDescription; }
}
