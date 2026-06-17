package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SitePersonnelId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;

import java.util.Objects;

/**
 * Entity representing a site personnel assignment (中心人员) within a clinical trial project.
 * <p>
 * Associates a user with a trial site and a specific role. Personnel assignments
 * can be enabled or disabled to control access to trial operations at the site level.
 * </p>
 *
 * <h3>State transitions</h3>
 * <ul>
 *   <li>A newly created assignment is always enabled ({@code disabled = false}).</li>
 *   <li>{@link #disable()} — Disables the assignment, preventing the user from
 *       performing their role at the site.</li>
 *   <li>{@link #enable()} — Re-enables a previously disabled assignment.</li>
 * </ul>
 */
public class SitePersonnel extends Entity<SitePersonnelId> {

    private SitePersonnelId id;
    private ProjectId projectId;
    private Long userId;
    private Long siteId;
    private SiteRole role;
    private boolean disabled;

    /**
     * Default constructor for persistence frameworks.
     */
    protected SitePersonnel() {
    }

    /**
     * Private constructor used by the factory method.
     */
    private SitePersonnel(SitePersonnelId id, ProjectId projectId, Long userId,
                           Long siteId, SiteRole role) {
        this.id = id;
        this.projectId = projectId;
        this.userId = userId;
        this.siteId = siteId;
        this.role = role;
        this.disabled = false;
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new SitePersonnel entity.
     *
     * @param id        the unique personnel assignment identity; must not be null
     * @param projectId the project this assignment belongs to; must not be null
     * @param userId    the user identity being assigned; must not be null
     * @param siteId    the site identity; must not be null
     * @param role      the role at the site; must not be null
     * @return a new SitePersonnel instance
     * @throws IllegalArgumentException if any parameter is null
     */
    public static SitePersonnel create(SitePersonnelId id, ProjectId projectId,
                                        Long userId, Long siteId, SiteRole role) {
        if (id == null) {
            throw new IllegalArgumentException("SitePersonnel id must not be null");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("SitePersonnel projectId must not be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("SitePersonnel userId must not be null");
        }
        if (siteId == null) {
            throw new IllegalArgumentException("SitePersonnel siteId must not be null");
        }
        if (role == null) {
            throw new IllegalArgumentException("SitePersonnel role must not be null");
        }

        return new SitePersonnel(id, projectId, userId, siteId, role);
    }

    // ---------------------------------------------------------------
    // Business methods
    // ---------------------------------------------------------------

    /**
     * Enables this personnel assignment.
     * <p>
     * If the assignment is already enabled, this is a no-op. Once enabled,
     * the user can perform their role at the assigned site.
     * </p>
     */
    public void enable() {
        this.disabled = false;
    }

    /**
     * Disables this personnel assignment.
     * <p>
     * A disabled assignment prevents the user from performing their role
     * at the site. This is a soft-disable that preserves the assignment
     * record for historical purposes.
     * </p>
     *
     * @throws BusinessRuleViolationException if the assignment is already disabled
     */
    public void disable() {
        if (this.disabled) {
            throw new BusinessRuleViolationException("PERSONNEL_ALREADY_DISABLED",
                    "Site personnel assignment [id=" + this.id + "] is already disabled");
        }
        this.disabled = true;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public SitePersonnelId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public SiteRole getRole() {
        return role;
    }

    public boolean isDisabled() {
        return disabled;
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(SitePersonnelId id) {
        this.id = id;
    }

    public void setProjectId(ProjectId projectId) {
        this.projectId = projectId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public void setRole(SiteRole role) {
        this.role = role;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "SitePersonnel{id=" + id + ", userId=" + userId
                + ", siteId=" + siteId + ", role=" + role + '}';
    }
}
