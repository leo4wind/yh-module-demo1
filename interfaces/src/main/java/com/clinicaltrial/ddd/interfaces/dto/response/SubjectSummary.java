package com.clinicaltrial.ddd.interfaces.dto.response;

/**
 * 受试者摘要（列表用）.
 */
public class SubjectSummary {

    private Long id;
    private Long projectId;
    private String code;
    private String status;
    private String blh;
    private String syxh;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBlh() { return blh; }
    public void setBlh(String blh) { this.blh = blh; }
    public String getSyxh() { return syxh; }
    public void setSyxh(String syxh) { this.syxh = syxh; }
}
