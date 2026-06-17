package com.clinicaltrial.ddd.statistics.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.util.Date;

/**
 * JPA entity mirroring the AnalysisResult domain entity.
 * Maps to the rd_analysis_result table.
 */
@Entity
@Table(name = "rd_analysis_result")
public class AnalysisResultJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "name", length = 200)
    private String name;

    @Column(name = "method", length = 200)
    private String method;

    @Lob
    @Column(name = "data")
    private String data;

    @Column(name = "result_summary", length = 2000)
    private String resultSummary;

    @Lob
    @Column(name = "params")
    private String params;

    @Column(name = "is_favorite")
    private Boolean isFavorite;

    @Column(name = "create_time")
    private Date createTime;

    public AnalysisResultJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Boolean getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
