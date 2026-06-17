package com.clinicaltrial.ddd.trial.application.command;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.FieldType;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Application command for replacing the editable structure of a CRF template.
 */
public class UpdateCrfTemplateCommand {

    private final String name;
    private final String code;
    private final String category;
    private final String estimateTime;
    private final String notice;
    private final String introduce;
    private final List<FormCommand> forms;

    public UpdateCrfTemplateCommand(String name, String code, String category,
                                    String estimateTime, String notice, String introduce,
                                    List<FormCommand> forms) {
        this.name = name;
        this.code = code;
        this.category = category;
        this.estimateTime = estimateTime;
        this.notice = notice;
        this.introduce = introduce;
        this.forms = forms != null ? forms : Collections.emptyList();
    }

    public String getName() { return name; }
    public String getCode() { return code; }
    public String getCategory() { return category; }
    public String getEstimateTime() { return estimateTime; }
    public String getNotice() { return notice; }
    public String getIntroduce() { return introduce; }
    public List<FormCommand> getForms() { return forms; }

    public static class FormCommand {
        private final String modelName;
        private final String refName;
        private final String rulesName;
        private final List<FieldCommand> fields;

        public FormCommand(String modelName, String refName, String rulesName,
                           List<FieldCommand> fields) {
            this.modelName = modelName;
            this.refName = refName;
            this.rulesName = rulesName;
            this.fields = fields != null ? fields : Collections.emptyList();
        }

        public String getModelName() { return modelName; }
        public String getRefName() { return refName; }
        public String getRulesName() { return rulesName; }
        public List<FieldCommand> getFields() { return fields; }
    }

    public static class FieldCommand {
        private final String fieldCode;
        private final String fieldLabel;
        private final FieldType fieldType;
        private final String defaultValue;
        private final String dataUnit;
        private final boolean required;
        private final boolean hidden;
        private final Integer sortOrder;
        private final List<OptionCommand> options;

        public FieldCommand(String fieldCode, String fieldLabel, FieldType fieldType,
                            String defaultValue, String dataUnit, boolean required,
                            boolean hidden, Integer sortOrder, List<OptionCommand> options) {
            this.fieldCode = fieldCode;
            this.fieldLabel = fieldLabel;
            this.fieldType = fieldType;
            this.defaultValue = defaultValue;
            this.dataUnit = dataUnit;
            this.required = required;
            this.hidden = hidden;
            this.sortOrder = sortOrder;
            this.options = options != null ? options : Collections.emptyList();
        }

        public String getFieldCode() { return fieldCode; }
        public String getFieldLabel() { return fieldLabel; }
        public FieldType getFieldType() { return fieldType; }
        public String getDefaultValue() { return defaultValue; }
        public String getDataUnit() { return dataUnit; }
        public boolean isRequired() { return required; }
        public boolean isHidden() { return hidden; }
        public Integer getSortOrder() { return sortOrder; }
        public List<OptionCommand> getOptions() { return options; }
    }

    public static class OptionCommand {
        private final String optionLabel;
        private final String optionValue;
        private final Integer sortOrder;
        private final BigDecimal score;

        public OptionCommand(String optionLabel, String optionValue, Integer sortOrder,
                             BigDecimal score) {
            this.optionLabel = optionLabel;
            this.optionValue = optionValue;
            this.sortOrder = sortOrder;
            this.score = score;
        }

        public String getOptionLabel() { return optionLabel; }
        public String getOptionValue() { return optionValue; }
        public Integer getSortOrder() { return sortOrder; }
        public BigDecimal getScore() { return score; }
    }
}
