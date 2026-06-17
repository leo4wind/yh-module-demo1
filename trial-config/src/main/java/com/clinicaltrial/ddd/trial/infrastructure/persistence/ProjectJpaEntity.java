package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.aggregate.Project} aggregate root.
 */
@Entity
@Table(name = "rd_project")
public class ProjectJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private ProjectType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProjectStatus status;

    @Column(name = "abbreviation", length = 100)
    private String abbreviation;

    @Column(name = "prefix", nullable = false, length = 50)
    private String prefix;

    @Column(name = "open_screen")
    private boolean openScreen;

    @Column(name = "expected_subject_size")
    private Integer expectedSubjectSize;

    @Column(name = "clinical_number", length = 100)
    private String clinicalNumber;

    @Column(name = "registration_no", length = 100)
    private String registrationNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "expect_start_at")
    private Date expectStartAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "expect_end_at")
    private Date expectEndAt;

    @Column(name = "site_model", length = 100)
    private String siteModel;

    @Column(name = "enable_question")
    private boolean enableQuestion;

    @Column(name = "enable_random_group")
    private boolean enableRandomGroup;

    @Column(name = "enable_blind")
    private boolean enableBlind;

    @Column(name = "purpose", length = 1000)
    private String purpose;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "create_user_id", nullable = false, length = 50)
    private String createUserId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gmt_create")
    private Date gmtCreate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "gmt_modified")
    private Date gmtModified;

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StageJpaEntity> stages = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SitePersonnelJpaEntity> sitePersonnel = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdverseEventRuleJpaEntity> adverseEventRules = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitPlanJpaEntity> visitPlans = new ArrayList<>();

    @OneToMany(mappedBy = "projectId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StageCrfBindingJpaEntity> crfBindings = new ArrayList<>();

    public ProjectJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public boolean isOpenScreen() {
        return openScreen;
    }

    public void setOpenScreen(boolean openScreen) {
        this.openScreen = openScreen;
    }

    public Integer getExpectedSubjectSize() {
        return expectedSubjectSize;
    }

    public void setExpectedSubjectSize(Integer expectedSubjectSize) {
        this.expectedSubjectSize = expectedSubjectSize;
    }

    public String getClinicalNumber() {
        return clinicalNumber;
    }

    public void setClinicalNumber(String clinicalNumber) {
        this.clinicalNumber = clinicalNumber;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public Date getExpectStartAt() {
        return expectStartAt;
    }

    public void setExpectStartAt(Date expectStartAt) {
        this.expectStartAt = expectStartAt;
    }

    public Date getExpectEndAt() {
        return expectEndAt;
    }

    public void setExpectEndAt(Date expectEndAt) {
        this.expectEndAt = expectEndAt;
    }

    public String getSiteModel() {
        return siteModel;
    }

    public void setSiteModel(String siteModel) {
        this.siteModel = siteModel;
    }

    public boolean isEnableQuestion() {
        return enableQuestion;
    }

    public void setEnableQuestion(boolean enableQuestion) {
        this.enableQuestion = enableQuestion;
    }

    public boolean isEnableRandomGroup() {
        return enableRandomGroup;
    }

    public void setEnableRandomGroup(boolean enableRandomGroup) {
        this.enableRandomGroup = enableRandomGroup;
    }

    public boolean isEnableBlind() {
        return enableBlind;
    }

    public void setEnableBlind(boolean enableBlind) {
        this.enableBlind = enableBlind;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public List<StageJpaEntity> getStages() {
        return stages;
    }

    public void setStages(List<StageJpaEntity> stages) {
        this.stages = stages;
    }

    public List<SitePersonnelJpaEntity> getSitePersonnel() {
        return sitePersonnel;
    }

    public void setSitePersonnel(List<SitePersonnelJpaEntity> sitePersonnel) {
        this.sitePersonnel = sitePersonnel;
    }

    public List<AdverseEventRuleJpaEntity> getAdverseEventRules() {
        return adverseEventRules;
    }

    public void setAdverseEventRules(List<AdverseEventRuleJpaEntity> adverseEventRules) {
        this.adverseEventRules = adverseEventRules;
    }

    public List<VisitPlanJpaEntity> getVisitPlans() {
        return visitPlans;
    }

    public void setVisitPlans(List<VisitPlanJpaEntity> visitPlans) {
        this.visitPlans = visitPlans;
    }

    public List<StageCrfBindingJpaEntity> getCrfBindings() {
        return crfBindings;
    }

    public void setCrfBindings(List<StageCrfBindingJpaEntity> crfBindings) {
        this.crfBindings = crfBindings;
    }
}
