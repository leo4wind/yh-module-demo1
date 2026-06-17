package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

/**
 * Application command for binding a CRF template to a trial stage.
 * <p>
 * Carries the data needed to create a StageCrfBinding entity linking a
 * CRF template version to a specific trial stage.
 * </p>
 */
public class BindCrfToStageCommand {

    private final ProjectId projectId;
    private final StageId stageId;
    private final CrfTemplateId crfId;
    private final CrfVersionId crfVersionId;
    private final boolean userInputEnabled;

    /**
     * Creates a new BindCrfToStageCommand.
     */
    public BindCrfToStageCommand(ProjectId projectId, StageId stageId,
                                  CrfTemplateId crfId, CrfVersionId crfVersionId,
                                  boolean userInputEnabled) {
        this.projectId = projectId;
        this.stageId = stageId;
        this.crfId = crfId;
        this.crfVersionId = crfVersionId;
        this.userInputEnabled = userInputEnabled;
    }

    public ProjectId getProjectId() { return projectId; }
    public StageId getStageId() { return stageId; }
    public CrfTemplateId getCrfId() { return crfId; }
    public CrfVersionId getCrfVersionId() { return crfVersionId; }
    public boolean isUserInputEnabled() { return userInputEnabled; }
}
