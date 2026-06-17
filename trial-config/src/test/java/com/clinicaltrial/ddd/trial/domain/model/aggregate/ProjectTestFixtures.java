package com.clinicaltrial.ddd.trial.domain.model.aggregate;

import com.clinicaltrial.ddd.trial.domain.model.entity.AdverseEventRule;
import com.clinicaltrial.ddd.trial.domain.model.entity.SitePersonnel;
import com.clinicaltrial.ddd.trial.domain.model.entity.Stage;
import com.clinicaltrial.ddd.trial.domain.model.entity.StageCrfBinding;
import com.clinicaltrial.ddd.trial.domain.model.entity.VisitPlan;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseEventRuleId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseJudgeType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.BaselineInterval;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SitePersonnelId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageCrfBindingId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Shared test fixtures for {@link ProjectTest}.
 * <p>
 * Provides factory methods for creating projects in various configuration states,
 * as well as commonly used value objects and commands.
 * </p>
 */
public final class ProjectTestFixtures {

    private ProjectTestFixtures() {
        // utility class
    }

    // ---------------------------------------------------------------
    // Identity / Value object helpers
    // ---------------------------------------------------------------

    static ProjectId aProjectId() {
        return new ProjectId(1L);
    }

    static StageId aStageId() {
        return new StageId(10L);
    }

    static VisitPlanId aVisitPlanId() {
        return new VisitPlanId(20L);
    }

    static StageCrfBindingId aBindingId() {
        return new StageCrfBindingId(30L);
    }

    static AdverseEventRuleId aRuleId() {
        return new AdverseEventRuleId(40L);
    }

    static SitePersonnelId aPersonnelId() {
        return new SitePersonnelId(50L);
    }

    static CrfTemplateId aCrfTemplateId() {
        return new CrfTemplateId(60L);
    }

    static CrfVersionId aCrfVersionId() {
        return new CrfVersionId(61L);
    }

    static BaselineInterval days3() {
        return new BaselineInterval(3L, "DAY");
    }

    static WindowPeriod plusMinus2() {
        return new WindowPeriod(2L, 2L);
    }

    // ---------------------------------------------------------------
    // Aggregate helpers
    // ---------------------------------------------------------------

    /**
     * Creates a new draft project via the factory method.
     */
    static Project aDraftProject() {
        return Project.create(
                aProjectId(),
                "Test Trial",
                ProjectType.INTERVENTIONAL,
                "TT",
                "TT",
                true,
                100,
                "NCT001",
                "R001",
                new Date(),
                new Date(),
                "Testing purpose",
                "user1");
    }

    /**
     * Creates a draft project with one stage already configured.
     */
    static Project aDraftProjectWithStage() {
        Project p = aDraftProject();
        p.addStage(aStageId(), "Screening", StageRepeatType.NONE,
                true, days3(), plusMinus2(), "task1");
        // Clear the events from adding the stage
        return p;
    }

    /**
     * Creates an active project (DRAFT -> stage added -> ACTIVE).
     */
    static Project anActiveProject() {
        Project p = aDraftProjectWithStage();
        p.activate();
        return p;
    }

    /**
     * Creates a draft project with multiple stages.
     */
    static Project aDraftProjectWithMultipleStages() {
        Project p = aDraftProject();
        p.addStage(new StageId(1L), "Screening", StageRepeatType.NONE,
                true, days3(), plusMinus2(), "task1");
        p.addStage(new StageId(2L), "Treatment", StageRepeatType.NONE,
                true, null, null, null);
        p.addStage(new StageId(3L), "Follow-up", StageRepeatType.WEEK,
                false, null, null, null);
        return p;
    }

    // ---------------------------------------------------------------
    // Reconstitution helper
    // ---------------------------------------------------------------

    /**
     * Creates a fully populated Project via reconstruct for testing.
     */
    static Project aReconstructedProject() {
        Stage stage = Stage.create(aStageId(), aProjectId(), "Screening",
                StageRepeatType.NONE, true, days3(), plusMinus2(), "task1");

        List<Stage> stages = new ArrayList<>();
        stages.add(stage);

        return Project.reconstruct(
                aProjectId(),
                "Reconstructed",
                ProjectType.OBSERVATIONAL,
                ProjectStatus.ACTIVE,
                "RECON",
                "RECON",
                false,
                50,
                "NCT002",
                "R002",
                new Date(),
                new Date(),
                "single",
                true,
                false,
                true,
                "Reconstruction test",
                "Test remarks",
                "reconstructor",
                new Date(),
                new Date(),
                stages,
                Collections.<SitePersonnel>emptyList(),
                Collections.<AdverseEventRule>emptyList(),
                Collections.<VisitPlan>emptyList(),
                Collections.<StageCrfBinding>emptyList());
    }
}
