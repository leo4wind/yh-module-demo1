package com.clinicaltrial.ddd.datacollection.domain.model.aggregate;

import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfFieldValueId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared test fixtures for {@link CrfAssessmentTest}.
 * <p>
 * Provides factory methods for creating CRF assessments in various lifecycle states,
 * field definitions, field values, and scoring rules. All helpers return fresh instances.
 * </p>
 */
public final class CrfAssessmentTestFixtures {

    private CrfAssessmentTestFixtures() {
        // utility class
    }

    // ---------------------------------------------------------------
    // Identity / Value object helpers
    // ---------------------------------------------------------------

    public static CrfAssessmentId aId() {
        return new CrfAssessmentId(1L);
    }

    public static SubjectId aSubjectId() {
        return new SubjectId(10L);
    }

    public static CrfTemplateId aCrfId() {
        return new CrfTemplateId(100L);
    }

    public static CrfVersionId aVersionId() {
        return new CrfVersionId(200L);
    }

    public static SubjectStageId aStageId() {
        return new SubjectStageId(300L);
    }

    public static CrfFieldValueId aFieldValueId() {
        return new CrfFieldValueId(1L);
    }

    // ---------------------------------------------------------------
    // CrfField helpers
    // ---------------------------------------------------------------

    public static CrfField requiredField(String code) {
        return new CrfField(code, true, false, false);
    }

    public static CrfField optionalField(String code) {
        return new CrfField(code, false, false, false);
    }

    public static CrfField hiddenField(String code) {
        return new CrfField(code, true, true, false);
    }

    public static CrfField conditionalHiddenField(String code) {
        return new CrfField(code, true, false, true);
    }

    // ---------------------------------------------------------------
    // CrfFieldValue helpers
    // ---------------------------------------------------------------

    public static CrfFieldValue fieldValue(String code, String value) {
        return new CrfFieldValue(
                new CrfFieldValueId(System.currentTimeMillis()),
                aId(), code, code, value, value, null, "TEXT", null, 1);
    }

    public static CrfFieldValue emptyFieldValue(String code) {
        return new CrfFieldValue(
                new CrfFieldValueId(System.currentTimeMillis()),
                aId(), code, code, "", "", null, "TEXT", null, 1);
    }

    // ---------------------------------------------------------------
    // Aggregate helpers in various lifecycle states
    // ---------------------------------------------------------------

    /**
     * Creates a CRF assessment in PENDING state with no field values.
     */
    public static CrfAssessment aPendingAssessment() {
        return CrfAssessment.create(aId(), aSubjectId(), aCrfId(), aVersionId(), aStageId());
    }

    /**
     * Creates a CRF assessment that has been moved to IN_PROGRESS state
     * with one field filled (50% on two required fields).
     */
    public static CrfAssessment anInProgressAssessment() {
        CrfAssessment assessment = aPendingAssessment();
        assessment.saveFieldValue(fieldValue("f1", "value1"), 1L);
        assessment.pullDomainEvents(); // clear field value change event
        assessment.calculateCompleteness(twoRequiredFields());
        assessment.pullDomainEvents(); // clear completeness changed event
        return assessment;
    }

    /**
     * Creates a CRF assessment in COMPLETED state with all required fields filled.
     */
    public static CrfAssessment aCompletedAssessment() {
        CrfAssessment assessment = aPendingAssessment();
        assessment.saveFieldValue(fieldValue("f1", "value1"), 1L);
        assessment.saveFieldValue(fieldValue("f2", "value2"), 1L);
        assessment.pullDomainEvents(); // clear field value change events
        assessment.calculateCompleteness(twoRequiredFields());
        assessment.pullDomainEvents(); // clear completion events
        return assessment;
    }

    /**
     * Creates a CRF assessment in QUERIED state.
     */
    public static CrfAssessment aQueriedAssessment() {
        CrfAssessment assessment = aCompletedAssessment();
        assessment.raiseQuery();
        assessment.pullDomainEvents(); // clear any events
        return assessment;
    }

    /**
     * Creates a CRF assessment in AUDITED state.
     */
    public static CrfAssessment anAuditedAssessment() {
        CrfAssessment assessment = aCompletedAssessment();
        assessment.audit(100L);
        assessment.pullDomainEvents(); // clear audit event
        return assessment;
    }

    // ---------------------------------------------------------------
    // Collection helpers
    // ---------------------------------------------------------------

    public static List<CrfField> twoRequiredFields() {
        return Arrays.asList(requiredField("f1"), requiredField("f2"));
    }

    public static List<CrfField> mixedFields() {
        return Arrays.asList(
                requiredField("f1"),
                requiredField("f2"),
                optionalField("f3"),
                hiddenField("f4"),
                conditionalHiddenField("f5")
        );
    }

    public static List<CrfField> allHiddenFields() {
        return Arrays.asList(hiddenField("f1"), hiddenField("f2"));
    }

    public static Map<String, BigDecimal> scoringRules() {
        Map<String, BigDecimal> rules = new HashMap<>();
        rules.put("f1", BigDecimal.ONE);
        rules.put("f2", BigDecimal.valueOf(2));
        return rules;
    }
}
