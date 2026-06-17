package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.BaselineInterval;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod;

import java.util.Objects;

/**
 * Entity representing a trial stage (研究阶段) within a clinical trial project.
 * <p>
 * A stage defines a discrete phase of a clinical trial, such as screening,
 * treatment, or follow-up. Stages can be configured with baseline intervals,
 * visit windows, and repeat schedules. They are the fundamental unit for
 * organizing trial timelines and CRF bindings.
 * </p>
 *
 * <h3>State transitions</h3>
 * <ul>
 *   <li>A newly created stage is always valid ({@code valid = true}).</li>
 *   <li>Marking a stage invalid via {@link #markInvalid()} prevents further
 *       use in visit planning and CRF binding, but does not remove it.</li>
 *   <li>Stages with {@code autoAdd = true} are automatically instantiated
 *       for subjects when they reach this stage in the workflow.</li>
 * </ul>
 */
public class Stage extends Entity<StageId> {

    private StageId id;
    private ProjectId projectId;
    private String name;
    private StageRepeatType repeatType;
    private boolean autoAdd;
    private boolean valid;
    private BaselineInterval baselineInterval;
    private WindowPeriod windowPeriod;
    private String taskFlag;

    /**
     * Default constructor for persistence frameworks (e.g., JPA, MyBatis).
     */
    protected Stage() {
    }

    /**
     * Private constructor used by the factory method.
     */
    private Stage(StageId id, ProjectId projectId, String name, StageRepeatType repeatType,
                  boolean autoAdd, BaselineInterval baselineInterval, WindowPeriod windowPeriod,
                  String taskFlag) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.repeatType = repeatType;
        this.autoAdd = autoAdd;
        this.valid = true;
        this.baselineInterval = baselineInterval;
        this.windowPeriod = windowPeriod;
        this.taskFlag = taskFlag;
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new Stage entity.
     *
     * @param id                the unique stage identity; must not be null
     * @param projectId         the project this stage belongs to; must not be null
     * @param name              the stage name (e.g., "筛选期", "治疗期"); must not be blank
     * @param repeatType        the repeat schedule type; must not be null
     * @param autoAdd           whether this stage should be automatically added for subjects
     * @param baselineInterval  the baseline time interval from a reference date; may be null
     * @param windowPeriod      the acceptable window around scheduled dates; may be null
     * @param taskFlag          an optional task flag for workflow integration; may be null
     * @return a new Stage instance
     * @throws IllegalArgumentException if id, projectId, name, or repeatType is null, or name is blank
     */
    public static Stage create(StageId id, ProjectId projectId, String name, StageRepeatType repeatType,
                                boolean autoAdd, BaselineInterval baselineInterval,
                                WindowPeriod windowPeriod, String taskFlag) {
        if (id == null) {
            throw new IllegalArgumentException("Stage id must not be null");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("Stage projectId must not be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Stage name must not be blank");
        }
        if (repeatType == null) {
            throw new IllegalArgumentException("Stage repeatType must not be null");
        }

        return new Stage(id, projectId, name.trim(), repeatType, autoAdd,
                baselineInterval, windowPeriod, taskFlag);
    }

    // ---------------------------------------------------------------
    // Business methods
    // ---------------------------------------------------------------

    /**
     * Configures the baseline interval for this stage.
     * <p>
     * The baseline interval defines the time offset from a reference date
     * (typically the subject's enrollment date or previous stage completion)
     * for scheduling purposes.
     * </p>
     *
     * @param baselineInterval the baseline interval to set; must not be null
     * @throws BusinessRuleViolationException if the stage is not valid
     */
    public void configureBaseline(BaselineInterval baselineInterval) {
        if (!this.valid) {
            throw new BusinessRuleViolationException("STAGE_INVALID",
                    "Cannot configure baseline on an invalid stage [id=" + this.id + "]");
        }
        if (baselineInterval == null) {
            throw new IllegalArgumentException("baselineInterval must not be null");
        }
        this.baselineInterval = baselineInterval;
    }

    /**
     * Configures the window period for this stage.
     * <p>
     * The window period defines the acceptable time range (before and after
     * the scheduled date) within which the stage activities can be performed.
     * </p>
     *
     * @param windowPeriod the window period to set; must not be null
     * @throws BusinessRuleViolationException if the stage is not valid
     */
    public void configureWindow(WindowPeriod windowPeriod) {
        if (!this.valid) {
            throw new BusinessRuleViolationException("STAGE_INVALID",
                    "Cannot configure window on an invalid stage [id=" + this.id + "]");
        }
        if (windowPeriod == null) {
            throw new IllegalArgumentException("windowPeriod must not be null");
        }
        this.windowPeriod = windowPeriod;
    }

    /**
     * Marks this stage as invalid.
     * <p>
     * An invalid stage cannot be used for visit planning or CRF binding.
     * This is a soft-disable that preserves the stage record for
     * historical reference.
     * </p>
     */
    public void markInvalid() {
        this.valid = false;
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public StageId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public StageRepeatType getRepeatType() {
        return repeatType;
    }

    public boolean isAutoAdd() {
        return autoAdd;
    }

    public boolean isValid() {
        return valid;
    }

    public BaselineInterval getBaselineInterval() {
        return baselineInterval;
    }

    public WindowPeriod getWindowPeriod() {
        return windowPeriod;
    }

    public String getTaskFlag() {
        return taskFlag;
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(StageId id) {
        this.id = id;
    }

    public void setProjectId(ProjectId projectId) {
        this.projectId = projectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRepeatType(StageRepeatType repeatType) {
        this.repeatType = repeatType;
    }

    public void setAutoAdd(boolean autoAdd) {
        this.autoAdd = autoAdd;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void setBaselineInterval(BaselineInterval baselineInterval) {
        this.baselineInterval = baselineInterval;
    }

    public void setWindowPeriod(WindowPeriod windowPeriod) {
        this.windowPeriod = windowPeriod;
    }

    public void setTaskFlag(String taskFlag) {
        this.taskFlag = taskFlag;
    }

    @Override
    public String toString() {
        return "Stage{id=" + id + ", name='" + name + "', valid=" + valid + '}';
    }
}
