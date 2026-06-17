package com.clinicaltrial.ddd.interfaces.dto.request;

import java.util.List;

/**
 * 入组受试者请求（已筛选受试者入组）.
 */
public class EnrollSubjectRequest {

    private Long projectId;
    private Long siteId;
    private Long userId;
    private String blh;
    private String syxh;
    private List<String> groupSubsetIds;

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getBlh() { return blh; }
    public void setBlh(String blh) { this.blh = blh; }
    public String getSyxh() { return syxh; }
    public void setSyxh(String syxh) { this.syxh = syxh; }
    public List<String> getGroupSubsetIds() { return groupSubsetIds; }
    public void setGroupSubsetIds(List<String> groupSubsetIds) { this.groupSubsetIds = groupSubsetIds; }
}
