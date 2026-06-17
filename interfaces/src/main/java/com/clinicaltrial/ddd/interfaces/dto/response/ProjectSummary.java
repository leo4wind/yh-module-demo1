package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.Date;

/**
 * 项目摘要（列表用）.
 */
public class ProjectSummary {

    private Long id;
    private String title;
    private String type;
    private String status;
    private String abbreviation;
    private Integer expectedSubjectSize;
    private Date gmtCreate;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public Integer getExpectedSubjectSize() { return expectedSubjectSize; }
    public void setExpectedSubjectSize(Integer expectedSubjectSize) { this.expectedSubjectSize = expectedSubjectSize; }
    public Date getGmtCreate() { return gmtCreate; }
    public void setGmtCreate(Date gmtCreate) { this.gmtCreate = gmtCreate; }
}
