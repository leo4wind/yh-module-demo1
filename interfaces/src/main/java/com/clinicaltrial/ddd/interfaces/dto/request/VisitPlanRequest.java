package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 配置随访计划请求.
 */
public class VisitPlanRequest {

    private String name;
    private Long sourceStageId;
    private Long targetStageId;
    private Integer baselineDays;
    private String baselineDirection;  // BEFORE / AFTER
    private Integer beforeDays;
    private Integer afterDays;
    private String crfComponentId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getSourceStageId() { return sourceStageId; }
    public void setSourceStageId(Long sourceStageId) { this.sourceStageId = sourceStageId; }
    public Long getTargetStageId() { return targetStageId; }
    public void setTargetStageId(Long targetStageId) { this.targetStageId = targetStageId; }
    public Integer getBaselineDays() { return baselineDays; }
    public void setBaselineDays(Integer baselineDays) { this.baselineDays = baselineDays; }
    public String getBaselineDirection() { return baselineDirection; }
    public void setBaselineDirection(String baselineDirection) { this.baselineDirection = baselineDirection; }
    public Integer getBeforeDays() { return beforeDays; }
    public void setBeforeDays(Integer beforeDays) { this.beforeDays = beforeDays; }
    public Integer getAfterDays() { return afterDays; }
    public void setAfterDays(Integer afterDays) { this.afterDays = afterDays; }
    public String getCrfComponentId() { return crfComponentId; }
    public void setCrfComponentId(String crfComponentId) { this.crfComponentId = crfComponentId; }
}
