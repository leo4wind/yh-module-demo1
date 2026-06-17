package com.clinicaltrial.ddd.trial.domain.model.entity;

import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseEventRuleId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseJudgeType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import java.util.Objects;

/**
 * Entity representing a rule for automatic adverse event detection (不良事件规则).
 * <p>
 * An adverse event rule defines conditions under which a CRF data entry should
 * automatically trigger an adverse event record. Rules can be based on specific
 * field values or logical expressions.
 * </p>
 *
 * <h3>Matching Logic</h3>
 * For {@link AdverseJudgeType#FIELD_VALUE} rules, the rule matches when both
 * the field code and value code match the incoming data. For
 * {@link AdverseJudgeType#LOGIC_EXPRESSION} rules, matching requires
 * evaluation of the logical expression (typically via a rule engine).
 * </p>
 */
public class AdverseEventRule extends Entity<AdverseEventRuleId> {

    private AdverseEventRuleId id;
    private ProjectId projectId;
    private StageId stageId;
    private CrfTemplateId crfId;
    private CrfVersionId crfVersionId;
    private AdverseJudgeType judgeType;
    private String fieldCode;
    private String fieldName;
    private String valueCode;
    private String valueName;

    /**
     * Default constructor for persistence frameworks.
     */
    protected AdverseEventRule() {
    }

    /**
     * Private constructor used by the factory method.
     */
    private AdverseEventRule(AdverseEventRuleId id, ProjectId projectId, StageId stageId,
                              CrfTemplateId crfId, CrfVersionId crfVersionId,
                              AdverseJudgeType judgeType, String fieldCode, String fieldName,
                              String valueCode, String valueName) {
        this.id = id;
        this.projectId = projectId;
        this.stageId = stageId;
        this.crfId = crfId;
        this.crfVersionId = crfVersionId;
        this.judgeType = judgeType;
        this.fieldCode = fieldCode;
        this.fieldName = fieldName;
        this.valueCode = valueCode;
        this.valueName = valueName;
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new AdverseEventRule entity.
     *
     * @param id           the unique rule identity; must not be null
     * @param projectId    the project this rule belongs to; must not be null
     * @param stageId      the stage this rule applies to; must not be null
     * @param crfId        the CRF template this rule monitors; must not be null
     * @param crfVersionId the specific CRF version; may be null (applies to all versions)
     * @param judgeType    the judgment type (FIELD_VALUE or LOGIC_EXPRESSION); must not be null
     * @param fieldCode    the CRF field code that triggers the rule (e.g., "AE001"); may be null for LOGIC_EXPRESSION
     * @param fieldName    the display name of the field; may be null
     * @param valueCode    the field value that triggers the rule (e.g., "YES"); may be null for LOGIC_EXPRESSION
     * @param valueName    the display name of the value; may be null
     * @return a new AdverseEventRule instance
     * @throws IllegalArgumentException if id, projectId, stageId, crfId, or judgeType is null
     */
    public static AdverseEventRule create(AdverseEventRuleId id, ProjectId projectId,
                                           StageId stageId, CrfTemplateId crfId,
                                           CrfVersionId crfVersionId, AdverseJudgeType judgeType,
                                           String fieldCode, String fieldName,
                                           String valueCode, String valueName) {
        if (id == null) {
            throw new IllegalArgumentException("AdverseEventRule id must not be null");
        }
        if (projectId == null) {
            throw new IllegalArgumentException("AdverseEventRule projectId must not be null");
        }
        if (stageId == null) {
            throw new IllegalArgumentException("AdverseEventRule stageId must not be null");
        }
        if (crfId == null) {
            throw new IllegalArgumentException("AdverseEventRule crfId must not be null");
        }
        if (judgeType == null) {
            throw new IllegalArgumentException("AdverseEventRule judgeType must not be null");
        }

        return new AdverseEventRule(id, projectId, stageId, crfId, crfVersionId,
                judgeType, fieldCode, fieldName, valueCode, valueName);
    }

    // ---------------------------------------------------------------
    // Business methods
    // ---------------------------------------------------------------

    /**
     * Checks whether this rule would trigger for the given field and value.
     * <p>
     * For {@link AdverseJudgeType#FIELD_VALUE} rules, the matching is
     * straightforward: the field code must match and the value code must match.
     * For {@link AdverseJudgeType#LOGIC_EXPRESSION} rules, this method
     * always returns {@code false} because complex expression evaluation
     * must be performed by a rule engine.
     * </p>
     *
     * @param fieldCode the CRF field code to check
     * @param valueCode the field value to check
     * @return {@code true} if this rule matches the given field and value
     * @throws IllegalArgumentException if either parameter is null
     */
    public boolean matches(String fieldCode, String valueCode) {
        if (fieldCode == null) {
            throw new IllegalArgumentException("fieldCode must not be null");
        }
        if (valueCode == null) {
            throw new IllegalArgumentException("valueCode must not be null");
        }

        if (judgeType == AdverseJudgeType.LOGIC_EXPRESSION) {
            // Logic expression rules require full expression evaluation
            // by a rule engine (e.g., Drools); simple matching is not sufficient.
            return false;
        }

        return this.fieldCode != null && this.fieldCode.equals(fieldCode)
                && this.valueCode != null && this.valueCode.equals(valueCode);
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public AdverseEventRuleId getId() {
        return id;
    }

    public ProjectId getProjectId() {
        return projectId;
    }

    public StageId getStageId() {
        return stageId;
    }

    public CrfTemplateId getCrfId() {
        return crfId;
    }

    public CrfVersionId getCrfVersionId() {
        return crfVersionId;
    }

    public AdverseJudgeType getJudgeType() {
        return judgeType;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValueCode() {
        return valueCode;
    }

    public String getValueName() {
        return valueName;
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(AdverseEventRuleId id) {
        this.id = id;
    }

    public void setProjectId(ProjectId projectId) {
        this.projectId = projectId;
    }

    public void setStageId(StageId stageId) {
        this.stageId = stageId;
    }

    public void setCrfId(CrfTemplateId crfId) {
        this.crfId = crfId;
    }

    public void setCrfVersionId(CrfVersionId crfVersionId) {
        this.crfVersionId = crfVersionId;
    }

    public void setJudgeType(AdverseJudgeType judgeType) {
        this.judgeType = judgeType;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    @Override
    public String toString() {
        return "AdverseEventRule{id=" + id + ", fieldCode='" + fieldCode
                + "', judgeType=" + judgeType + '}';
    }
}
