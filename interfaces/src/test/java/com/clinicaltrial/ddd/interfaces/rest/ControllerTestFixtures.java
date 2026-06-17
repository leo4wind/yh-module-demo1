package com.clinicaltrial.ddd.interfaces.rest;

import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfFieldValueId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.MonitoringStatus;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SnapshotValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageStatus;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportStatus;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.FileFormat;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryType;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Static factory methods returning mock domain objects for controller tests.
 */
public final class ControllerTestFixtures {

    private ControllerTestFixtures() {
        // utility class
    }

    // ========== Existing fixtures ==========

    /**
     * Creates a Project in DRAFT status with id=1L, title="Test Project", prefix="TT".
     */
    public static Project aProject() {
        return Project.reconstruct(
                new ProjectId(1L),
                "Test Project",
                ProjectType.INTERVENTIONAL,
                ProjectStatus.DRAFT,
                "TP",
                "TT",
                false,
                100,
                "CHN-TEST-001",
                "REG-001",
                new Date(),
                new Date(),
                null,
                false,
                false,
                false,
                "Test purpose",
                null,
                "user1",
                new Date(),
                new Date(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    /**
     * Creates a Subject in ACTIVE status with id=1L, code="TT-0001".
     */
    public static Subject aSubject() {
        return Subject.reconstruct(
                new SubjectId(1L),
                new com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId(1L),
                new SubjectCode("TT", 1),
                SubjectStatus.ACTIVE,
                100L,
                200L,
                "BLH-001",
                "SYXH-001",
                new ArrayList<String>(),
                null,
                null,
                null,
                null,
                null
        );
    }

    // ========== Fixtures for BC3: Data Collection ==========

    /**
     * Creates a SubjectStage in IN_PROGRESS status with id=1L, subjectId=10L, stageId=100L.
     */
    public static SubjectStage aSubjectStage() {
        return SubjectStage.reconstruct(
                new SubjectStageId(1L),
                new SubjectId(10L),
                new StageId(100L),
                null,
                SubjectStageStatus.IN_PROGRESS,
                new Date(),
                null,
                null,
                null,
                null,
                null,
                null,
                Collections.emptyList()
        );
    }

    /**
     * Creates a CrfAssessment in COMPLETED status with id=1L, 100% completeness, no field values.
     */
    public static CrfAssessment aCrfAssessment() {
        return CrfAssessment.reconstruct(
                new CrfAssessmentId(1L),
                new SubjectId(10L),
                new CrfTemplateId(200L),
                new CrfVersionId(300L),
                new SubjectStageId(1L),
                MonitoringStatus.COMPLETED,
                new Completeness(new BigDecimal("100.00"), 5, 5),
                false,
                null,
                Collections.emptyList()
        );
    }

    /**
     * Creates a CrfAssessment in IN_PROGRESS status with id=2L, 50% completeness,
     * containing two field values.
     */
    public static CrfAssessment aCrfAssessmentWithFieldValues() {
        CrfFieldValue fv1 = new CrfFieldValue(
                new CrfFieldValueId(10L),
                new CrfAssessmentId(2L),
                "btbm00",
                "btms00",
                "value1",
                "Value One",
                "kg",
                "text",
                null,
                1
        );
        CrfFieldValue fv2 = new CrfFieldValue(
                new CrfFieldValueId(11L),
                new CrfAssessmentId(2L),
                "dabm00",
                "dams00",
                "value2",
                "Value Two",
                null,
                "number",
                null,
                2
        );
        java.util.List<CrfFieldValue> fieldValues = new ArrayList<>();
        fieldValues.add(fv1);
        fieldValues.add(fv2);

        return CrfAssessment.reconstruct(
                new CrfAssessmentId(2L),
                new SubjectId(10L),
                new CrfTemplateId(200L),
                new CrfVersionId(300L),
                new SubjectStageId(1L),
                MonitoringStatus.IN_PROGRESS,
                new Completeness(new BigDecimal("50.00"), 1, 2),
                false,
                null,
                fieldValues
        );
    }

    // ========== Fixtures for BC4: Query Management ==========

    /**
     * Creates a Query in OPEN status with id=1L, assessmentId=1L, MONITOR_QUERY type.
     */
    public static Query aQuery() {
        return Query.reconstruct(
                new QueryId(1L),
                new CrfAssessmentId(1L),
                new QueryFieldIdentifier("btbm00", "", "text"),
                QueryStatus.OPEN,
                QueryType.MONITOR_QUERY,
                "Data discrepancy?",
                null,
                null,
                100L,
                null,
                new Date(),
                null,
                new SnapshotValue("btbm00", "btms00", "val", "valText", new Date()),
                null
        );
    }

    // ========== Fixtures for BC5: Data Export ==========

    /**
     * Creates an ExportTask in DRAFT status with id=1L, taskName="Test Export", CSV format.
     */
    public static ExportTask anExportTask() {
        return ExportTask.reconstruct(
                new ExportTaskId(1L),
                "Test Export",
                new com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId(1L),
                null,
                null,
                ExportStatus.DRAFT,
                FileFormat.CSV,
                null,
                null,
                null,
                null,
                null,
                0,
                0,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    // ========== Fixtures for BC6: Statistical Analysis ==========

    /**
     * Creates an AnalysisProject with id=1L, name="Test Analysis",
     * description="Description", and empty child collections.
     */
    public static AnalysisProject anAnalysisProject() {
        return AnalysisProject.reconstruct(
                new AnalysisProjectId(1L),
                "Test Analysis",
                "Description",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
    }
}
