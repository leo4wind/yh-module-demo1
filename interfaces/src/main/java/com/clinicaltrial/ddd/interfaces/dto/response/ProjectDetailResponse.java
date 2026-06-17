package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.Date;
import java.util.List;

/**
 * 项目详情响应.
 */
public class ProjectDetailResponse {

    private Long id;
    private String title;
    private String type;
    private String status;
    private String abbreviation;
    private String prefix;
    private Boolean openScreen;
    private Integer expectedSubjectSize;
    private String clinicalNumber;
    private String registrationNo;
    private Date expectStartAt;
    private Date expectEndAt;
    private String siteModel;
    private Boolean enableQuestion;
    private Boolean enableRandomGroup;
    private Boolean enableBlind;
    private String purpose;
    private String remarks;
    private String createUserId;
    private Date gmtCreate;
    private Date gmtModified;

    private List<StageVo> stages;
    private List<VisitPlanVo> visitPlans;
    private List<CrfBindingVo> crfBindings;
    private List<SitePersonnelVo> sitePersonnel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAbbreviation() { return abbreviation; }
    public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
    public String getPrefix() { return prefix; }
    public void setPrefix(String prefix) { this.prefix = prefix; }
    public Boolean getOpenScreen() { return openScreen; }
    public void setOpenScreen(Boolean openScreen) { this.openScreen = openScreen; }
    public Integer getExpectedSubjectSize() { return expectedSubjectSize; }
    public void setExpectedSubjectSize(Integer expectedSubjectSize) { this.expectedSubjectSize = expectedSubjectSize; }
    public String getClinicalNumber() { return clinicalNumber; }
    public void setClinicalNumber(String clinicalNumber) { this.clinicalNumber = clinicalNumber; }
    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }
    public Date getExpectStartAt() { return expectStartAt; }
    public void setExpectStartAt(Date expectStartAt) { this.expectStartAt = expectStartAt; }
    public Date getExpectEndAt() { return expectEndAt; }
    public void setExpectEndAt(Date expectEndAt) { this.expectEndAt = expectEndAt; }
    public String getSiteModel() { return siteModel; }
    public void setSiteModel(String siteModel) { this.siteModel = siteModel; }
    public Boolean getEnableQuestion() { return enableQuestion; }
    public void setEnableQuestion(Boolean enableQuestion) { this.enableQuestion = enableQuestion; }
    public Boolean getEnableRandomGroup() { return enableRandomGroup; }
    public void setEnableRandomGroup(Boolean enableRandomGroup) { this.enableRandomGroup = enableRandomGroup; }
    public Boolean getEnableBlind() { return enableBlind; }
    public void setEnableBlind(Boolean enableBlind) { this.enableBlind = enableBlind; }
    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }
    public Date getGmtCreate() { return gmtCreate; }
    public void setGmtCreate(Date gmtCreate) { this.gmtCreate = gmtCreate; }
    public Date getGmtModified() { return gmtModified; }
    public void setGmtModified(Date gmtModified) { this.gmtModified = gmtModified; }
    public List<StageVo> getStages() { return stages; }
    public void setStages(List<StageVo> stages) { this.stages = stages; }
    public List<VisitPlanVo> getVisitPlans() { return visitPlans; }
    public void setVisitPlans(List<VisitPlanVo> visitPlans) { this.visitPlans = visitPlans; }
    public List<CrfBindingVo> getCrfBindings() { return crfBindings; }
    public void setCrfBindings(List<CrfBindingVo> crfBindings) { this.crfBindings = crfBindings; }
    public List<SitePersonnelVo> getSitePersonnel() { return sitePersonnel; }
    public void setSitePersonnel(List<SitePersonnelVo> sitePersonnel) { this.sitePersonnel = sitePersonnel; }

    // ---- Inner VOs ----

    public static class StageVo {
        private Long id;
        private String name;
        private String repeatType;
        private Boolean autoAdd;
        private Integer baselineDays;
        private String baselineDirection;
        private Integer beforeDays;
        private Integer afterDays;
        private String taskFlag;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
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

    public static class VisitPlanVo {
        private Long id;
        private String name;
        private Long sourceStageId;
        private Long targetStageId;
        private Integer baselineDays;
        private String baselineDirection;
        private Integer beforeDays;
        private Integer afterDays;
        private String crfComponentId;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
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

    public static class CrfBindingVo {
        private Long id;
        private Long stageId;
        private Long crfId;
        private Long crfVersionId;
        private Boolean userInputEnabled;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getStageId() { return stageId; }
        public void setStageId(Long stageId) { this.stageId = stageId; }
        public Long getCrfId() { return crfId; }
        public void setCrfId(Long crfId) { this.crfId = crfId; }
        public Long getCrfVersionId() { return crfVersionId; }
        public void setCrfVersionId(Long crfVersionId) { this.crfVersionId = crfVersionId; }
        public Boolean getUserInputEnabled() { return userInputEnabled; }
        public void setUserInputEnabled(Boolean userInputEnabled) { this.userInputEnabled = userInputEnabled; }
    }

    public static class SitePersonnelVo {
        private Long id;
        private Long userId;
        private Long siteId;
        private String role;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getSiteId() { return siteId; }
        public void setSiteId(Long siteId) { this.siteId = siteId; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}
