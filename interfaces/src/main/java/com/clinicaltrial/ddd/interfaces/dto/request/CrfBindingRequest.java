package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 绑定CRF请求.
 */
public class CrfBindingRequest {

    private Long stageId;
    private Long crfId;
    private Long crfVersionId;
    private Boolean userInputEnabled;

    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public Long getCrfId() { return crfId; }
    public void setCrfId(Long crfId) { this.crfId = crfId; }
    public Long getCrfVersionId() { return crfVersionId; }
    public void setCrfVersionId(Long crfVersionId) { this.crfVersionId = crfVersionId; }
    public Boolean getUserInputEnabled() { return userInputEnabled; }
    public void setUserInputEnabled(Boolean userInputEnabled) { this.userInputEnabled = userInputEnabled; }
}
