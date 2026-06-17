package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;

/**
 * Application command for assigning personnel to a trial site.
 * <p>
 * Carries the data needed to create a SitePersonnel entity linking a user
 * to a specific trial site with a designated role.
 * </p>
 */
public class AssignSitePersonnelCommand {

    private final ProjectId projectId;
    private final Long userId;
    private final Long siteId;
    private final SiteRole role;

    /**
     * Creates a new AssignSitePersonnelCommand.
     */
    public AssignSitePersonnelCommand(ProjectId projectId, Long userId,
                                       Long siteId, SiteRole role) {
        this.projectId = projectId;
        this.userId = userId;
        this.siteId = siteId;
        this.role = role;
    }

    public ProjectId getProjectId() { return projectId; }
    public Long getUserId() { return userId; }
    public Long getSiteId() { return siteId; }
    public SiteRole getRole() { return role; }
}
