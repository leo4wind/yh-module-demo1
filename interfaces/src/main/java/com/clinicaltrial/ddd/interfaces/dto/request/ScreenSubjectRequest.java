package com.clinicaltrial.ddd.interfaces.dto.request;

import java.time.LocalDate;

/**
 * 筛选受试者请求.
 */
public class ScreenSubjectRequest {

    private Long projectId;
    private Long siteId;
    private Long userId;
    private LocalDate screeningDate;
    private String screeningResult;  // PASS / FAIL
    private String remarks;
    private String blh;
    private String syxh;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDate getScreeningDate() { return screeningDate; }
    public void setScreeningDate(LocalDate screeningDate) { this.screeningDate = screeningDate; }
    public String getScreeningResult() { return screeningResult; }
    public void setScreeningResult(String screeningResult) { this.screeningResult = screeningResult; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getBlh() { return blh; }
    public void setBlh(String blh) { this.blh = blh; }
    public String getSyxh() { return syxh; }
    public void setSyxh(String syxh) { this.syxh = syxh; }
}
