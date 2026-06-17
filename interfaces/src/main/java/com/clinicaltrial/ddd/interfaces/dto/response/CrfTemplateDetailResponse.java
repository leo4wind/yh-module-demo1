package com.clinicaltrial.ddd.interfaces.dto.response;

import java.math.BigDecimal;
import java.util.List;

/**
 * CRF模板详情响应.
 */
public class CrfTemplateDetailResponse extends CrfTemplateSummary {

    private String notice;
    private String introduce;
    private List<FormVo> forms;

    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }
    public String getIntroduce() { return introduce; }
    public void setIntroduce(String introduce) { this.introduce = introduce; }
    public List<FormVo> getForms() { return forms; }
    public void setForms(List<FormVo> forms) { this.forms = forms; }

    public static class FormVo {
        private Long id;
        private String modelName;
        private String refName;
        private String rulesName;
        private List<FieldVo> fields;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        public String getRefName() { return refName; }
        public void setRefName(String refName) { this.refName = refName; }
        public String getRulesName() { return rulesName; }
        public void setRulesName(String rulesName) { this.rulesName = rulesName; }
        public List<FieldVo> getFields() { return fields; }
        public void setFields(List<FieldVo> fields) { this.fields = fields; }
    }

    public static class FieldVo {
        private Long id;
        private String fieldCode;
        private String fieldLabel;
        private String fieldType;
        private String defaultValue;
        private String dataUnit;
        private Boolean required;
        private Boolean hidden;
        private Integer sortOrder;
        private List<OptionVo> options;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
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
        public List<OptionVo> getOptions() { return options; }
        public void setOptions(List<OptionVo> options) { this.options = options; }
    }

    public static class OptionVo {
        private Long id;
        private String optionLabel;
        private String optionValue;
        private Integer sortOrder;
        private BigDecimal score;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
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
