package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

/**
 * Application command for configuring a visit plan in a clinical trial project.
 * <p>
 * Carries the data needed to create a visit plan linking a source stage to
 * a target stage with timing intervals and window periods.
 * </p>
 */
public class ConfigureVisitPlanCommand {

    private final ProjectId projectId;
    private final String name;
    private final StageId sourceStageId;
    private final StageId targetStageId;
    private final Long baselineInterval;
    private final String baselineIntervalUnit;
    private final Long windowBeforeDays;
    private final Long windowAfterDays;
    private final String crfComponentId;

    /**
     * Creates a new ConfigureVisitPlanCommand.
     */
    public ConfigureVisitPlanCommand(ProjectId projectId, String name,
                                      StageId sourceStageId, StageId targetStageId,
                                      Long baselineInterval, String baselineIntervalUnit,
                                      Long windowBeforeDays, Long windowAfterDays,
                                      String crfComponentId) {
        this.projectId = projectId;
        this.name = name;
        this.sourceStageId = sourceStageId;
        this.targetStageId = targetStageId;
        this.baselineInterval = baselineInterval;
        this.baselineIntervalUnit = baselineIntervalUnit;
        this.windowBeforeDays = windowBeforeDays;
        this.windowAfterDays = windowAfterDays;
        this.crfComponentId = crfComponentId;
    }

    public ProjectId getProjectId() { return projectId; }
    public String getName() { return name; }
    public StageId getSourceStageId() { return sourceStageId; }
    public StageId getTargetStageId() { return targetStageId; }
    public Long getBaselineInterval() { return baselineInterval; }
    public String getBaselineIntervalUnit() { return baselineIntervalUnit; }
    public Long getWindowBeforeDays() { return windowBeforeDays; }
    public Long getWindowAfterDays() { return windowAfterDays; }
    public String getCrfComponentId() { return crfComponentId; }
}
