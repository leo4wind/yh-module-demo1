package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseJudgeType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

/**
 * Application command for configuring an adverse event detection rule.
 * <p>
 * Carries the data needed to create an AdverseEventRule entity for automatic
 * detection of adverse events based on CRF field values or logical expressions.
 * </p>
 */
public class ConfigureAdverseRuleCommand {

    private final ProjectId projectId;
    private final StageId stageId;
    private final CrfTemplateId crfId;
    private final CrfVersionId crfVersionId;
    private final AdverseJudgeType judgeType;
    private final String fieldCode;
    private final String fieldName;
    private final String valueCode;
    private final String valueName;

    /**
     * Creates a new ConfigureAdverseRuleCommand.
     */
    public ConfigureAdverseRuleCommand(ProjectId projectId, StageId stageId,
                                        CrfTemplateId crfId, CrfVersionId crfVersionId,
                                        AdverseJudgeType judgeType, String fieldCode,
                                        String fieldName, String valueCode, String valueName) {
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

    public ProjectId getProjectId() { return projectId; }
    public StageId getStageId() { return stageId; }
    public CrfTemplateId getCrfId() { return crfId; }
    public CrfVersionId getCrfVersionId() { return crfVersionId; }
    public AdverseJudgeType getJudgeType() { return judgeType; }
    public String getFieldCode() { return fieldCode; }
    public String getFieldName() { return fieldName; }
    public String getValueCode() { return valueCode; }
    public String getValueName() { return valueName; }
}
