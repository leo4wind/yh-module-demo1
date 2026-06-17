package com.clinicaltrial.ddd.interfaces.dto.response;

import java.time.LocalDate;
import java.util.List;

/**
 * 受试者详情响应.
 */
public class SubjectDetailResponse {

    private Long id;
    private Long projectId;
    private String code;
    private String status;
    private Long userId;
    private Long siteId;
    private String blh;
    private String syxh;
    private List<String> groupSubsetIds;
    private ScreeningInfoVo screeningInfo;
    private SubjectFallOffVo fallOffReason;
    private String remarks;
    private Long trackDownId;
    private Long supervisorId;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public String getBlh() { return blh; }
    public void setBlh(String blh) { this.blh = blh; }
    public String getSyxh() { return syxh; }
    public void setSyxh(String syxh) { this.syxh = syxh; }
    public List<String> getGroupSubsetIds() { return groupSubsetIds; }
    public void setGroupSubsetIds(List<String> groupSubsetIds) { this.groupSubsetIds = groupSubsetIds; }
    public ScreeningInfoVo getScreeningInfo() { return screeningInfo; }
    public void setScreeningInfo(ScreeningInfoVo screeningInfo) { this.screeningInfo = screeningInfo; }
    public SubjectFallOffVo getFallOffReason() { return fallOffReason; }
    public void setFallOffReason(SubjectFallOffVo fallOffReason) { this.fallOffReason = fallOffReason; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Long getTrackDownId() { return trackDownId; }
    public void setTrackDownId(Long trackDownId) { this.trackDownId = trackDownId; }
    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }

    public static class ScreeningInfoVo {
        private LocalDate screeningDate;
        private String screeningResult;
        private String remarks;

        public LocalDate getScreeningDate() { return screeningDate; }
        public void setScreeningDate(LocalDate screeningDate) { this.screeningDate = screeningDate; }
        public String getScreeningResult() { return screeningResult; }
        public void setScreeningResult(String screeningResult) { this.screeningResult = screeningResult; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class SubjectFallOffVo {
        private String reasonCode;
        private String reasonDescription;
        private LocalDate fallOffDate;

        public String getReasonCode() { return reasonCode; }
        public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }
        public String getReasonDescription() { return reasonDescription; }
        public void setReasonDescription(String reasonDescription) { this.reasonDescription = reasonDescription; }
        public LocalDate getFallOffDate() { return fallOffDate; }
        public void setFallOffDate(LocalDate fallOffDate) { this.fallOffDate = fallOffDate; }
    }
}
