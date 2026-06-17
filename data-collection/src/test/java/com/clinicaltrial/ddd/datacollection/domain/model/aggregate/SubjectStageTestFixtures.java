package com.clinicaltrial.ddd.datacollection.domain.model.aggregate;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.BaselineTime;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.util.Date;

/**
 * Shared test fixtures for {@link SubjectStageTest}.
 * <p>
 * Provides factory methods for creating subject stages in various lifecycle states
 * and commonly used value objects. All helpers return fresh instances.
 * </p>
 */
public final class SubjectStageTestFixtures {

    private SubjectStageTestFixtures() {
        // utility class
    }

    // ---------------------------------------------------------------
    // Identity / Value object helpers
    // ---------------------------------------------------------------

    static SubjectStageId aSubjectStageId() {
        return new SubjectStageId(1L);
    }

    static SubjectId aSubjectId() {
        return new SubjectId(10L);
    }

    static StageId aTrialStageId() {
        return new StageId(100L);
    }

    static VisitPlanId aPlanEventId() {
        return new VisitPlanId(200L);
    }

    static BaselineTime aBaselineTime() {
        return new BaselineTime(new Date(), "visit");
    }

    static CrfAssessmentId aCrfAssessmentId() {
        return new CrfAssessmentId(1L);
    }

    static CrfAssessmentId aCrfAssessmentId(long value) {
        return new CrfAssessmentId(value);
    }

    // ---------------------------------------------------------------
    // Aggregate helpers in various lifecycle states
    // ---------------------------------------------------------------

    /**
     * Creates a subject stage in PENDING state.
     */
    static SubjectStage aPendingStage() {
        return SubjectStage.create(
                aSubjectStageId(), aSubjectId(), aTrialStageId(),
                aPlanEventId(), aBaselineTime());
    }

    /**
     * Creates a subject stage in IN_PROGRESS state.
     */
    static SubjectStage aStartedStage() {
        SubjectStage stage = aPendingStage();
        stage.pullDomainEvents(); // clear generation event
        stage.start();
        return stage;
    }

    /**
     * Creates a subject stage in COMPLETED state.
     */
    static SubjectStage aCompletedStage() {
        SubjectStage stage = aStartedStage();
        stage.pullDomainEvents(); // clear start events
        stage.complete(new java.util.HashSet<>(), 100L);
        stage.pullDomainEvents(); // clear completion event
        return stage;
    }
}
