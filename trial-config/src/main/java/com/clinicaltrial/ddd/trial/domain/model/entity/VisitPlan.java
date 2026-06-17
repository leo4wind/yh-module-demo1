package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.BaselineInterval;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * Entity representing a visit plan (访视计划) within a clinical trial project.
 * <p>
 * A visit plan defines the timing of a follow-up visit or assessment relative
 * to a source stage. It specifies a target date calculated from the source
 * stage's baseline plus an offset, with an allowable window period around
 * the target.
 * </p>
 *
 * <h3>Timing Calculation</h3>
 * The target date for a visit is calculated as:
 * <pre>
 * targetDate = sourceDate + baselineInterval
 * visit is on-schedule if targetDate - beforeDays &lt;= actualDate &lt;= targetDate + afterDays
 * </pre>
 */
public class VisitPlan extends Entity<VisitPlanId> {

    private VisitPlanId id;
    private ProjectId projectId;
    private String name;
    private StageId sourceStageId;
    private StageId targetStageId;
    private BaselineInterval baselineInterval;
    private WindowPeriod windowPeriod;
    private String crfComponentId;

    /**
     * Default constructor for persistence frameworks.
     */
    protected VisitPlan() {
    }

    /**
     * Private constructor used by the factory method.
     */
    private VisitPlan(VisitPlanId id, ProjectId projectId, String name,
                      StageId sourceStageId, StageId targetStageId,
                      BaselineInterval baselineInterval, WindowPeriod windowPeriod,
                      String crfComponentId) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.sourceStageId = sourceStageId;
        this.targetStageId = targetStageId;
        this.baselineInterval = baselineInterval;
        this.windowPeriod = windowPeriod;
        this.crfComponentId = crfComponentId;
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new VisitPlan entity.
     *
     * @param id                the unique visit plan identity; must not be null
     * @param projectId         the project this plan belongs to; must not be null
     * @param name              the plan name (e.g., "治疗后第1周访视"); must not be blank
     * @param sourceStageId     the source stage whose completion triggers this visit; must not be null
     * @param targetStageId     the target stage that this visit schedules; must not be null
     * @param baselineInterval  the baseline offset from the source stage date; may be null
     * @param windowPeriod      the acceptable window around the target date; may be null
     * @param crfComponentId    the CRF component (date field) that triggers the follow-up; may be null
     * @return a new VisitPlan instance
     * @throws IllegalArgumentException if any required parameter is null, or name is blank
     */
    public static VisitPlan create(VisitPlanId id, ProjectId projectId, String name,
                                    StageId sourceStageId, StageId targetStageId,
                                    BaselineInterval baselineInterval, WindowPeriod windowPeriod,
                                    String crfComponentId) {
        if (id == null) {
            throw new IllegalArgumentException("VisitPlan id must not be null");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("VisitPlan projectId must not be null");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("VisitPlan name must not be blank");
        }
        if (sourceStageId == null) {
            throw new IllegalArgumentException("VisitPlan sourceStageId must not be null");
        }
        if (targetStageId == null) {
            throw new IllegalArgumentException("VisitPlan targetStageId must not be null");
        }

        return new VisitPlan(id, projectId, name.trim(), sourceStageId, targetStageId,
                baselineInterval, windowPeriod, crfComponentId);
    }

    // ---------------------------------------------------------------
    // Business methods
    // ---------------------------------------------------------------

    /**
     * Calculates the target date for this visit plan based on a source date.
     * <p>
     * The target date is calculated by adding the baseline interval to the source
     * date. If no baseline interval is configured, the source date itself is
     * returned as the target date.
     * </p>
     *
     * @param sourceDate the reference date (e.g., subject enrollment date or
     *                   source stage completion date); must not be null
     * @return the calculated target date
     * @throws IllegalArgumentException if sourceDate is null
     */
    public Date calculateTargetDate(Date sourceDate) {
        if (sourceDate == null) {
            throw new IllegalArgumentException("sourceDate must not be null");
        }

        if (baselineInterval == null) {
            return new Date(sourceDate.getTime());
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceDate);

        String unit = baselineInterval.getUnit();
        long interval = baselineInterval.getInterval();

        switch (unit) {
            case "DAY":
                calendar.add(Calendar.DAY_OF_YEAR, (int) interval);
                break;
            case "WEEK":
                calendar.add(Calendar.WEEK_OF_YEAR, (int) interval);
                break;
            case "MONTH":
                calendar.add(Calendar.MONTH, (int) interval);
                break;
            default:
                throw new IllegalStateException("Unexpected baseline interval unit: " + unit);
        }

        return calendar.getTime();
    }

    /**
     * Checks whether this visit plan is within the acceptable window period
     * relative to a source date and an actual visit date.
     *
     * @param sourceDate the reference source date
     * @param actualDate the actual visit date to check
     * @return {@code true} if the actual date falls within the window, or
     *         {@code true} if no window is configured (no restriction)
     * @throws IllegalArgumentException if either parameter is null
     */
    public boolean isWithinWindow(Date sourceDate, Date actualDate) {
        if (sourceDate == null) {
            throw new IllegalArgumentException("sourceDate must not be null");
        }
        if (actualDate == null) {
            throw new IllegalArgumentException("actualDate must not be null");
        }

        if (windowPeriod == null) {
            return true;
        }

        Date targetDate = calculateTargetDate(sourceDate);
        return windowPeriod.contains(targetDate, actualDate);
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public VisitPlanId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public StageId getSourceStageId() {
        return sourceStageId;
    }

    public StageId getTargetStageId() {
        return targetStageId;
    }

    public BaselineInterval getBaselineInterval() {
        return baselineInterval;
    }

    public WindowPeriod getWindowPeriod() {
        return windowPeriod;
    }

    public String getCrfComponentId() {
        return crfComponentId;
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(VisitPlanId id) {
        this.id = id;
    }

    public void setProjectId(ProjectId projectId) {
        this.projectId = projectId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSourceStageId(StageId sourceStageId) {
        this.sourceStageId = sourceStageId;
    }

    public void setTargetStageId(StageId targetStageId) {
        this.targetStageId = targetStageId;
    }

    public void setBaselineInterval(BaselineInterval baselineInterval) {
        this.baselineInterval = baselineInterval;
    }

    public void setWindowPeriod(WindowPeriod windowPeriod) {
        this.windowPeriod = windowPeriod;
    }

    public void setCrfComponentId(String crfComponentId) {
        this.crfComponentId = crfComponentId;
    }

    @Override
    public String toString() {
        return "VisitPlan{id=" + id + ", name='" + name + "', source=" + sourceStageId
                + ", target=" + targetStageId + '}';
    }
}
