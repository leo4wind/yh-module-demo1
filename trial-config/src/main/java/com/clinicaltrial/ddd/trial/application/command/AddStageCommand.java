package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;

/**
 * Application command for adding a stage to a clinical trial project.
 * <p>
 * Carries the data needed to create a new stage entity within a project.
 * The baseline interval and window period are specified as numeric values
 * with unit strings (e.g., "14 DAYS" or "3 MONTHS") so the application
 * service can construct the appropriate value objects.
 * </p>
 */
public class AddStageCommand {

    private final ProjectId projectId;
    private final String name;
    private final StageRepeatType repeatType;
    private final boolean autoAdd;
    private final Long baselineInterval;
    private final String baselineIntervalUnit;
    private final Long windowBeforeDays;
    private final Long windowAfterDays;

    /**
     * Creates a new AddStageCommand.
     */
    public AddStageCommand(ProjectId projectId, String name, StageRepeatType repeatType,
                            boolean autoAdd, Long baselineInterval, String baselineIntervalUnit,
                            Long windowBeforeDays, Long windowAfterDays) {
        this.projectId = projectId;
        this.name = name;
        this.repeatType = repeatType;
        this.autoAdd = autoAdd;
        this.baselineInterval = baselineInterval;
        this.baselineIntervalUnit = baselineIntervalUnit;
        this.windowBeforeDays = windowBeforeDays;
        this.windowAfterDays = windowAfterDays;
    }

    public ProjectId getProjectId() { return projectId; }
    public String getName() { return name; }
    public StageRepeatType getRepeatType() { return repeatType; }
    public boolean isAutoAdd() { return autoAdd; }
    public Long getBaselineInterval() { return baselineInterval; }
    public String getBaselineIntervalUnit() { return baselineIntervalUnit; }
    public Long getWindowBeforeDays() { return windowBeforeDays; }
    public Long getWindowAfterDays() { return windowAfterDays; }
}
