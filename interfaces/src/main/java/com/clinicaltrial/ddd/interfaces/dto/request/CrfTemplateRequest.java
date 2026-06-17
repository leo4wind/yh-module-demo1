package com.clinicaltrial.ddd.interfaces.dto.request;

import java.math.BigDecimal;
import java.util.List;

/**
 * CRF模板保存请求.
 */
public class CrfTemplateRequest {

    private String name;
    private String code;
    private String category;
    private String estimateTime;
    private String notice;
    private String introduce;
    private List<FormRequest> forms;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getEstimateTime() { return estimateTime; }
    public void setEstimateTime(String estimateTime) { this.estimateTime = estimateTime; }
    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }
    public String getIntroduce() { return introduce; }
    public void setIntroduce(String introduce) { this.introduce = introduce; }
    public List<FormRequest> getForms() { return forms; }
    public void setForms(List<FormRequest> forms) { this.forms = forms; }

    public static class FormRequest {
        private String modelName;
        private String refName;
        private String rulesName;
        private List<FieldRequest> fields;

        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public String getRefName() { return refName; }
        public void setRefName(String refName) { this.refName = refName; }
        public String getRulesName() { return rulesName; }
        public void setRulesName(String rulesName) { this.rulesName = rulesName; }
        public List<FieldRequest> getFields() { return fields; }
        public void setFields(List<FieldRequest> fields) { this.fields = fields; }
    }

    public static class FieldRequest {
        private String fieldCode;
        private String fieldLabel;
        private String fieldType;
        private String defaultValue;
        private String dataUnit;
        private Boolean required;
        private Boolean hidden;
        private Integer sortOrder;
        private List<OptionRequest> options;

        public String getFieldCode() { return fieldCode; }
        public void setFieldCode(String fieldCode) { this.fieldCode = fieldCode; }
        public String getFieldLabel() { return fieldLabel; }
        public void setFieldLabel(String fieldLabel) { this.fieldLabel = fieldLabel; }
        public String getFieldType() { return fieldType; }
        public void setFieldType(String fieldType) { this.fieldType = fieldType; }
        public String getDefaultValue() { return defaultValue; }
        public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }
        public String getDataUnit() { return dataUnit; }
        public void setDataUnit(String dataUnit) { this.dataUnit = dataUnit; }
        public Boolean getRequired() { return required; }
        public void setRequired(Boolean required) { this.required = required; }
        public Boolean getHidden() { return hidden; }
        public void setHidden(Boolean hidden) { this.hidden = hidden; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
        public List<OptionRequest> getOptions() { return options; }
        public void setOptions(List<OptionRequest> options) { this.options = options; }
    }

    public static class OptionRequest {
        private String optionLabel;
        private String optionValue;
        private Integer sortOrder;
        private BigDecimal score;

        public String getOptionLabel() { return optionLabel; }
        public void setOptionLabel(String optionLabel) { this.optionLabel = optionLabel; }
        public String getOptionValue() { return optionValue; }
        public void setOptionValue(String optionValue) { this.optionValue = optionValue; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
    }
}
