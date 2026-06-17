package com.clinicaltrial.ddd.subject.infrastructure.persistence;

import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject} aggregate root.
 */
@Entity
@Table(name = "rd_subject")
public class SubjectJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "code", length = 50)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private SubjectStatus status;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "site_id")
    private Long siteId;

    @Column(name = "blh", length = 100)
    private String blh;

    @Column(name = "syxh", length = 100)
    private String syxh;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "rd_subject_group", joinColumns = @JoinColumn(name = "subject_id"))
    @Column(name = "group_subset_id", length = 100)
    private List<String> groupSubsetIds = new ArrayList<>();

    @Embedded
    private ScreeningInfoJpa screeningInfo;

    @Embedded
    private SubjectFallOffReasonJpa fallOffReason;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "track_down_id")
    private Long trackDownId;

    @Column(name = "supervisor_id")
    private Long supervisorId;

    public SubjectJpaEntity() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SubjectStatus getStatus() {
        return status;
    }

    public void setStatus(SubjectStatus status) {
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getBlh() {
        return blh;
    }

    public void setBlh(String blh) {
        this.blh = blh;
    }

    public String getSyxh() {
        return syxh;
    }

    public void setSyxh(String syxh) {
        this.syxh = syxh;
    }

    public List<String> getGroupSubsetIds() {
        return groupSubsetIds;
    }

    public void setGroupSubsetIds(List<String> groupSubsetIds) {
        this.groupSubsetIds = groupSubsetIds;
    }

    public ScreeningInfoJpa getScreeningInfo() {
        return screeningInfo;
    }

    public void setScreeningInfo(ScreeningInfoJpa screeningInfo) {
        this.screeningInfo = screeningInfo;
    }

    public SubjectFallOffReasonJpa getFallOffReason() {
        return fallOffReason;
    }

    public void setFallOffReason(SubjectFallOffReasonJpa fallOffReason) {
        this.fallOffReason = fallOffReason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Long getTrackDownId() {
        return trackDownId;
    }

    public void setTrackDownId(Long trackDownId) {
        this.trackDownId = trackDownId;
    }

    public Long getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(Long supervisorId) {
        this.supervisorId = supervisorId;
    }
}
