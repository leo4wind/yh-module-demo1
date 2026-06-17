package com.clinicaltrial.ddd.trial.domain.model.valueobject;

/**
 * Enum representing the repeat type of a trial stage (阶段重复类型).
 * <p>
 * Defines whether a stage is a one-time occurrence or repeats on a schedule.
 * Repeat types are used for stages that need to be performed multiple times
 * during the trial, such as periodic follow-up visits.
 * </p>
 *
 * <ul>
 *   <li>{@link #NONE} — Stage does not repeat (single occurrence).</li>
 *   <li>{@link #DAY} — Stage repeats every N days.</li>
 *   <li>{@link #WEEK} — Stage repeats every N weeks.</li>
 *   <li>{@link #MONTH} — Stage repeats every N months.</li>
 * </ul>
 */
public enum StageRepeatType {

    NONE,
    DAY,
    WEEK,
    MONTH
}
