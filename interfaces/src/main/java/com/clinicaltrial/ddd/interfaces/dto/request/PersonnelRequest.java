package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 分配人员请求.
 */
public class PersonnelRequest {

    private Long userId;
    private Long siteId;
    private String role;  // SiteRole name

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
