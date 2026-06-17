package com.clinicaltrial.ddd.interfaces.dto.response;

/**
 * CRF模板摘要响应.
 */
public class CrfTemplateSummary {

    private Long id;
    private String name;
    private String code;
    private Long defaultVersionId;
    private String status;
    private String category;
    private String estimateTime;
    private Integer formCount;
    private Integer fieldCount;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Long getDefaultVersionId() { return defaultVersionId; }
    public void setDefaultVersionId(Long defaultVersionId) { this.defaultVersionId = defaultVersionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getEstimateTime() { return estimateTime; }
    public void setEstimateTime(String estimateTime) { this.estimateTime = estimateTime; }
    public Integer getFormCount() { return formCount; }
    public void setFormCount(Integer formCount) { this.formCount = formCount; }
    public Integer getFieldCount() { return fieldCount; }
    public void setFieldCount(Integer fieldCount) { this.fieldCount = fieldCount; }
}
