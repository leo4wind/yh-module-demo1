package com.clinicaltrial.ddd.interfaces.dto.response;

import java.util.Date;
import java.util.List;

/**
 * 受试者访视列表项.
 */
public class SubjectStageResponse {

    private Long id;
    private Long subjectId;
    private Long stageId;
    private String stageName;
    private String status;
    private Date plannedDate;
    private Date actualDate;
    private List<CrfAssessmentSummary> crfAssessments;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public Long getStageId() { return stageId; }
    public void setStageId(Long stageId) { this.stageId = stageId; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getPlannedDate() { return plannedDate; }
    public void setPlannedDate(Date plannedDate) { this.plannedDate = plannedDate; }
    public Date getActualDate() { return actualDate; }
    public void setActualDate(Date actualDate) { this.actualDate = actualDate; }
    public List<CrfAssessmentSummary> getCrfAssessments() { return crfAssessments; }
    public void setCrfAssessments(List<CrfAssessmentSummary> crfAssessments) { this.crfAssessments = crfAssessments; }

    /**
     * CRF评估摘要.
     */
    public static class CrfAssessmentSummary {
        private Long id;
        private Long crfTemplateId;
        private String crfName;
        private String status;
        private Double completeness;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getCrfTemplateId() { return crfTemplateId; }
        public void setCrfTemplateId(Long crfTemplateId) { this.crfTemplateId = crfTemplateId; }
        public String getCrfName() { return crfName; }
        public void setCrfName(String crfName) { this.crfName = crfName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Double getCompleteness() { return completeness; }
        public void setCompleteness(Double completeness) { this.completeness = completeness; }
    }
}
