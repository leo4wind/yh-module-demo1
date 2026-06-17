package com.clinicaltrial.ddd.interfaces.dto.request;

/**
 * 添加阶段请求.
 */
public class StageRequest {

    private String name;
    private String repeatType;       // StageRepeatType name
    private Boolean autoAdd;
    private Integer baselineDays;     // BaselineInterval days (nullable)
    private String baselineDirection; // BaselineInterval direction: BEFORE / AFTER (nullable)
    private Integer beforeDays;       // WindowPeriod beforeDays (nullable)
    private Integer afterDays;        // WindowPeriod afterDays (nullable)
    private String taskFlag;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRepeatType() { return repeatType; }
    public void setRepeatType(String repeatType) { this.repeatType = repeatType; }
    public Boolean getAutoAdd() { return autoAdd; }
    public void setAutoAdd(Boolean autoAdd) { this.autoAdd = autoAdd; }
    public Integer getBaselineDays() { return baselineDays; }
    public void setBaselineDays(Integer baselineDays) { this.baselineDays = baselineDays; }
    public String getBaselineDirection() { return baselineDirection; }
    public void setBaselineDirection(String baselineDirection) { this.baselineDirection = baselineDirection; }
    public Integer getBeforeDays() { return beforeDays; }
    public void setBeforeDays(Integer beforeDays) { this.beforeDays = beforeDays; }
    public Integer getAfterDays() { return afterDays; }
    public void setAfterDays(Integer afterDays) { this.afterDays = afterDays; }
    public String getTaskFlag() { return taskFlag; }
    public void setTaskFlag(String taskFlag) { this.taskFlag = taskFlag; }
}
