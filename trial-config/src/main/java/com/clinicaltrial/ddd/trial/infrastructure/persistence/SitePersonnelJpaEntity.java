package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.SitePersonnel} domain entity.
 */
@Entity
@Table(name = "rd_project_personnel")
public class SitePersonnelJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "site_id")
    private Long siteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private SiteRole role;

    @Column(name = "disabled")
    private boolean disabled;

    public SitePersonnelJpaEntity() {
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

    public SiteRole getRole() {
        return role;
    }

    public void setRole(SiteRole role) {
        this.role = role;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
