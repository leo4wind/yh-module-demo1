package com.clinicaltrial.ddd.interfaces.dto.request;

import java.util.List;

/**
 * 直接入组请求（跳过筛选）.
 */
public class DirectEnrollRequest {

    private Long projectId;
    private Long siteId;
    private Long userId;
    private String blh;
    private String syxh;
    private String name;
    private String gender;
    private Integer age;
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public List<String> getGroupSubsetIds() { return groupSubsetIds; }
    public void setGroupSubsetIds(List<String> groupSubsetIds) { this.groupSubsetIds = groupSubsetIds; }
}
