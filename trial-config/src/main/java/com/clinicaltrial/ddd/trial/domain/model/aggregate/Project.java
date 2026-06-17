package com.clinicaltrial.ddd.trial.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.trial.domain.event.CrfBoundToStageEvent;
import com.clinicaltrial.ddd.trial.domain.event.ProjectActivatedEvent;
import com.clinicaltrial.ddd.trial.domain.event.ProjectCreatedEvent;
import com.clinicaltrial.ddd.trial.domain.event.StageAddedEvent;
import com.clinicaltrial.ddd.trial.domain.event.VisitPlanConfiguredEvent;
import com.clinicaltrial.ddd.trial.domain.model.entity.AdverseEventRule;
import com.clinicaltrial.ddd.trial.domain.model.entity.SitePersonnel;
import com.clinicaltrial.ddd.trial.domain.model.entity.Stage;
import com.clinicaltrial.ddd.trial.domain.model.entity.StageCrfBinding;
import com.clinicaltrial.ddd.trial.domain.model.entity.VisitPlan;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseEventRuleId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseJudgeType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.BaselineInterval;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SitePersonnelId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageCrfBindingId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Aggregate Root for Trial Configuration (研究方案配置 - BC1).
 * <p>
 * Represents a clinical trial project and serves as the root entity for all
 * trial configuration concerns, including stages, visit plans, CRF bindings,
 * adverse event rules, and site personnel assignments.
 * </p>
 *
 * <h3>State Machine</h3>
 * <pre>
 * DRAFT (配置中) → ACTIVE (进行中) → CLOSED (已结束)
 * </pre>
 *
 * <ul>
 *   <li><strong>DRAFT:</strong> Initial state upon creation. Configuration changes
 *       (stages, CRF bindings, visits) are permitted.</li>
 *   <li><strong>ACTIVE:</strong> Trial is in progress. Subject enrollment, data
 *       collection, and monitoring are active. Configuration is locked.</li>
 *   <li><strong>CLOSED:</strong> Trial has ended. No further modifications or
 *       data collection are permitted.</li>
 * </ul>
 *
 * <h3>Business Rules</h3>
 * <ul>
 *   <li>Configuration changes (addStage, removeStage, bindCrf, etc.) are only
 *       allowed while the project is in DRAFT status.</li>
 *   <li>Activation requires at least one stage to be configured.</li>
 *   <li>Stage names must be unique within a project.</li>
 * </ul>
 */
public class Project extends AggregateRoot<ProjectId> {

    private ProjectId id;
    private String title;
    private ProjectType type;
    private ProjectStatus status;
    private String abbreviation;
    private String prefix;
    private boolean openScreen;
    private Integer expectedSubjectSize;
    private String clinicalNumber;
    private String registrationNo;
    private Date expectStartAt;
    private Date expectEndAt;
    private String siteModel;
    private boolean enableQuestion;
    private boolean enableRandomGroup;
    private boolean enableBlind;
    private String purpose;
    private String remarks;
    private String createUserId;
    private Date gmtCreate;
    private Date gmtModified;

    private List<Stage> stages;
    private List<SitePersonnel> sitePersonnel;
    private List<AdverseEventRule> adverseEventRules;
    private List<VisitPlan> visitPlans;
    private List<StageCrfBinding> crfBindings;

    /**
     * Default constructor for persistence frameworks.
     */
    protected Project() {
        this.stages = new ArrayList<>();
        this.sitePersonnel = new ArrayList<>();
        this.adverseEventRules = new ArrayList<>();
        this.visitPlans = new ArrayList<>();
        this.crfBindings = new ArrayList<>();
    }

    /**
     * Private constructor used by the factory method and reconstitution.
     */
    private Project(ProjectId id, String title, ProjectType type, String abbreviation,
                    String prefix, boolean openScreen, Integer expectedSubjectSize,
                    String clinicalNumber, String registrationNo, Date expectStartAt,
                    Date expectEndAt, String siteModel, boolean enableQuestion,
                    boolean enableRandomGroup, boolean enableBlind, String purpose,
                    String remarks, String createUserId) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.status = ProjectStatus.DRAFT;
        this.abbreviation = abbreviation;
        this.prefix = prefix;
        this.openScreen = openScreen;
        this.expectedSubjectSize = expectedSubjectSize;
        this.clinicalNumber = clinicalNumber;
        this.registrationNo = registrationNo;
        this.expectStartAt = expectStartAt;
        this.expectEndAt = expectEndAt;
        this.siteModel = siteModel;
        this.enableQuestion = enableQuestion;
        this.enableRandomGroup = enableRandomGroup;
        this.enableBlind = enableBlind;
        this.purpose = purpose;
        this.remarks = remarks;
        this.createUserId = createUserId;
        this.gmtCreate = new Date();
        this.gmtModified = new Date();
        this.stages = new ArrayList<>();
        this.sitePersonnel = new ArrayList<>();
        this.adverseEventRules = new ArrayList<>();
        this.visitPlans = new ArrayList<>();
        this.crfBindings = new ArrayList<>();
    }

    // ---------------------------------------------------------------
    // Factory method
    // ---------------------------------------------------------------

    /**
     * Creates a new Project aggregate in DRAFT status.
     * <p>
     * Raises a {@link ProjectCreatedEvent} that must be published after persistence.
     * </p>
     *
     * @param id                  the unique project identity; must not be null
     * @param title               the project title; must not be blank
     * @param type                the project type (INTERVENTIONAL or OBSERVATIONAL); must not be null
     * @param abbreviation        the project abbreviation; may be null
     * @param prefix              the project prefix code; must not be blank
     * @param openScreen          whether screening is open (no pre-screening required)
     * @param expectedSubjectSize the expected number of subjects; may be null
     * @param clinicalNumber      the clinical trial registration number; may be null
     * @param registrationNo      the internal registration number; may be null
     * @param expectStartAt       the expected start date; may be null
     * @param expectEndAt         the expected end date; may be null
     * @param purpose             the trial purpose/objective; may be null
     * @param createUserId        the user creating the project; must not be null
     * @return a new Project instance
     * @throws IllegalArgumentException if id, title, type, prefix, or createUserId is null
     */
    public static Project create(ProjectId id, String title, ProjectType type,
                                  String abbreviation, String prefix,
                                  boolean openScreen, Integer expectedSubjectSize,
                                  String clinicalNumber, String registrationNo,
                                  Date expectStartAt, Date expectEndAt,
                                  String purpose, String createUserId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Project title must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Project type must not be null");
        }
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Project prefix must not be blank");
        }
        if (createUserId == null || createUserId.trim().isEmpty()) {
            throw new IllegalArgumentException("Project createUserId must not be blank");
        }

        Project project = new Project(id, title.trim(), type,
                abbreviation != null ? abbreviation.trim() : null,
                prefix.trim(), openScreen, expectedSubjectSize,
                clinicalNumber, registrationNo, expectStartAt, expectEndAt,
                null, false, false, false,
                purpose, null, createUserId.trim());

        project.registerEvent(new ProjectCreatedEvent(
                project.id, project.title, project.createUserId, LocalDateTime.now()));

        return project;
    }

    // ---------------------------------------------------------------
    // State transition methods
    // ---------------------------------------------------------------

    /**
     * Activates the project, transitioning from DRAFT to ACTIVE status.
     * <p>
     * Activation requires at least one stage to be configured. Once activated,
     * configuration changes (stage, CRF, visit plan) are no longer allowed.
     * </p>
     *
     * @throws BusinessRuleViolationException if the project is not in DRAFT status,
     *                                        or if no stages have been configured
     */
    public void activate() {
        if (status != ProjectStatus.DRAFT) {
            throw new BusinessRuleViolationException("PROJECT_NOT_DRAFT",
                    "Only projects in DRAFT status can be activated. Current status: " + status);
        }
        if (stages == null || stages.isEmpty()) {
            throw new BusinessRuleViolationException("PROJECT_NO_STAGES",
                    "Cannot activate a project with no stages configured");
        }

        this.status = ProjectStatus.ACTIVE;
        this.gmtModified = new Date();

        registerEvent(new ProjectActivatedEvent(this.id, LocalDateTime.now()));
    }

    /**
     * Closes the project, transitioning from ACTIVE to CLOSED status.
     * <p>
     * Once closed, no further modifications or data collection are permitted.
     * Closing from DRAFT is not allowed; DRAFT projects should be deleted instead.
     * </p>
     *
     * @throws BusinessRuleViolationException if the project is not in ACTIVE status
     */
    public void close() {
        if (status != ProjectStatus.ACTIVE) {
            throw new BusinessRuleViolationException("PROJECT_NOT_ACTIVE",
                    "Only projects in ACTIVE status can be closed. Current status: " + status);
        }

        this.status = ProjectStatus.CLOSED;
        this.gmtModified = new Date();
    }

    // ---------------------------------------------------------------
    // Stage management
    // ---------------------------------------------------------------

    /**
     * Adds a new stage to the project.
     * <p>
     * Raises a {@link StageAddedEvent} that must be published after persistence.
     * </p>
     *
     * @param stageId           the identity for the new stage; must not be null
     * @param name              the stage name; must not be blank
     * @param repeatType        the repeat type; must not be null
     * @param autoAdd           whether to auto-add subjects to this stage
     * @param baselineInterval  the baseline interval; may be null
     * @param windowPeriod      the window period; may be null
     * @param taskFlag          an optional task flag; may be null
     * @throws BusinessRuleViolationException if the project is not in DRAFT status,
     *                                        or if a stage with the same name already exists
     */
    public void addStage(StageId stageId, String name, StageRepeatType repeatType,
                          boolean autoAdd, BaselineInterval baselineInterval,
                          WindowPeriod windowPeriod, String taskFlag) {
        assertDraftStatus();

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Stage name must not be blank");
        }
        if (repeatType == null) {
            throw new IllegalArgumentException("Stage repeatType must not be null");
        }

        // Check for duplicate stage names
        String trimmedName = name.trim();
        boolean duplicateName = stages.stream()
                .anyMatch(s -> s.getName().equalsIgnoreCase(trimmedName));
        if (duplicateName) {
            throw new BusinessRuleViolationException("DUPLICATE_STAGE_NAME",
                    "A stage with name '" + trimmedName + "' already exists in this project");
        }

        Stage stage = Stage.create(stageId, this.id, trimmedName, repeatType,
                autoAdd, baselineInterval, windowPeriod, taskFlag);
        this.stages.add(stage);
        this.gmtModified = new Date();

        registerEvent(new StageAddedEvent(this.id, stageId, trimmedName, LocalDateTime.now()));
    }

    /**
     * Removes a stage from the project.
     * <p>
     * This is a soft removal check: if subjects are already assigned to this
     * stage in an active project, the removal is rejected. However, since
     * subject assignment is managed by a different bounded context, the
     * actual subject count check is delegated to the caller or infrastructure.
     * </p>
     *
     * @param stageId the identity of the stage to remove; must not be null
     * @return {@code true} if the stage was removed
     * @throws BusinessRuleViolationException if the project is not in DRAFT status
     */
    public boolean removeStage(StageId stageId) {
        assertDraftStatus();
        if (stageId == null) {
            throw new IllegalArgumentException("stageId must not be null");
        }

        boolean removed = this.stages.removeIf(s -> s.getId().equals(stageId));
        if (removed) {
            this.gmtModified = new Date();
        }
        return removed;
    }

    // ---------------------------------------------------------------
    // Visit plan management
    // ---------------------------------------------------------------

    /**
     * Configures a visit plan linking a source stage to a target stage.
     * <p>
     * Raises a {@link VisitPlanConfiguredEvent} that must be published after persistence.
     * </p>
     *
     * @param visitPlanId       the identity for the new visit plan; must not be null
     * @param name              the visit plan name; must not be blank
     * @param sourceStageId     the source stage; must not be null
     * @param targetStageId     the target stage; must not be null
     * @param baselineInterval  the baseline interval from the source date; may be null
     * @param windowPeriod      the window around the target date; may be null
     * @param crfComponentId    the CRF component triggering the follow-up; may be null
     * @throws BusinessRuleViolationException if the project is not in DRAFT status
     */
    public void configureVisitPlan(VisitPlanId visitPlanId, String name,
                                    StageId sourceStageId, StageId targetStageId,
                                    BaselineInterval baselineInterval,
                                    WindowPeriod windowPeriod, String crfComponentId) {
        assertDraftStatus();

        VisitPlan visitPlan = VisitPlan.create(visitPlanId, this.id, name,
                sourceStageId, targetStageId, baselineInterval, windowPeriod,
                crfComponentId);
        this.visitPlans.add(visitPlan);
        this.gmtModified = new Date();

        registerEvent(new VisitPlanConfiguredEvent(
                this.id, visitPlanId, sourceStageId, targetStageId, LocalDateTime.now()));
    }

    // ---------------------------------------------------------------
    // CRF binding management
    // ---------------------------------------------------------------

    /**
     * Binds a CRF template to a stage.
     * <p>
     * Raises a {@link CrfBoundToStageEvent} that must be published after persistence.
     * </p>
     *
     * @param stageCrfBindingId the identity for the new binding; must not be null
     * @param stageId           the stage to bind to; must not be null
     * @param crfId             the CRF template to bind; must not be null
     * @param crfVersionId      the specific CRF version; may be null
     * @param userInputEnabled  whether direct user input is allowed
     * @throws BusinessRuleViolationException if the project is not in DRAFT status
     *                                        or if the stage is not found
     */
    public void bindCrf(StageCrfBindingId stageCrfBindingId, StageId stageId,
                         CrfTemplateId crfId, CrfVersionId crfVersionId,
                         boolean userInputEnabled) {
        assertDraftStatus();

        // Verify the stage exists in this project
        boolean stageExists = stages.stream().anyMatch(s -> s.getId().equals(stageId));
        if (!stageExists) {
            throw new BusinessRuleViolationException("STAGE_NOT_FOUND",
                    "Stage [id=" + stageId + "] not found in project [id=" + this.id + "]");
        }

        StageCrfBinding binding = StageCrfBinding.create(
                stageCrfBindingId, this.id, stageId, crfId, crfVersionId, userInputEnabled);
        this.crfBindings.add(binding);
        this.gmtModified = new Date();

        registerEvent(new CrfBoundToStageEvent(
                stageId, crfId, crfVersionId, LocalDateTime.now()));
    }

    // ---------------------------------------------------------------
    // Adverse event rule management
    // ---------------------------------------------------------------

    /**
     * Configures an adverse event detection rule for a specific stage and CRF field.
     *
     * @param ruleId       the identity for the new rule; must not be null
     * @param stageId      the stage this rule applies to; must not be null
     * @param crfId        the CRF template this rule monitors; must not be null
     * @param crfVersionId the specific CRF version; may be null
     * @param judgeType    the judgment type; must not be null
     * @param fieldCode    the CRF field code; may be null for LOGIC_EXPRESSION
     * @param fieldName    the field display name; may be null
     * @param valueCode    the triggering value code; may be null for LOGIC_EXPRESSION
     * @param valueName    the value display name; may be null
     * @throws BusinessRuleViolationException if the project is not in DRAFT status
     */
    public void configureAdverseRule(AdverseEventRuleId ruleId, StageId stageId,
                                      CrfTemplateId crfId, CrfVersionId crfVersionId,
                                      AdverseJudgeType judgeType, String fieldCode,
                                      String fieldName, String valueCode, String valueName) {
        assertDraftStatus();

        AdverseEventRule rule = AdverseEventRule.create(
                ruleId, this.id, stageId, crfId, crfVersionId,
                judgeType, fieldCode, fieldName, valueCode, valueName);
        this.adverseEventRules.add(rule);
        this.gmtModified = new Date();
    }

    // ---------------------------------------------------------------
    // Site personnel management
    // ---------------------------------------------------------------

    /**
     * Assigns a user to a trial site with a specific role.
     *
     * @param personnelId the identity for the new assignment; must not be null
     * @param userId      the user identity; must not be null
     * @param siteId      the site identity; must not be null
     * @param role        the role at the site; must not be null
     * @throws BusinessRuleViolationException if the project is not in DRAFT status
     */
    public void assignPersonnel(SitePersonnelId personnelId, Long userId,
                                 Long siteId, SiteRole role) {
        assertDraftStatus();

        SitePersonnel person = SitePersonnel.create(personnelId, this.id, userId, siteId, role);
        this.sitePersonnel.add(person);
        this.gmtModified = new Date();
    }

    // ---------------------------------------------------------------
    // Query methods
    // ---------------------------------------------------------------

    /**
     * Returns all stages configured for auto-add behavior.
     * <p>
     * Auto-add stages are those that should be automatically instantiated
     * for each subject when they reach that stage in the workflow.
     * </p>
     *
     * @return a list of stages with {@code autoAdd = true}
     */
    public List<Stage> getAutoAddStages() {
        if (stages == null || stages.isEmpty()) {
            return Collections.emptyList();
        }
        List<Stage> result = new ArrayList<>();
        for (Stage stage : stages) {
            if (stage.isAutoAdd()) {
                result.add(stage);
            }
        }
        return Collections.unmodifiableList(result);
    }

    // ---------------------------------------------------------------
    // Reconstitution factory (persistence only)
    // ---------------------------------------------------------------

    /**
     * Reconstitutes a Project aggregate from persistence.
     * <p>
     * This factory is used exclusively by repositories to rebuild a Project
     * from stored data. Unlike {@link #create(...)}, it does not raise any
     * domain events.
     * </p>
     *
     * @param id                  the project identity
     * @param title               the project title
     * @param type                the project type
     * @param status              the current project status
     * @param abbreviation        the project abbreviation
     * @param prefix              the project prefix
     * @param openScreen          whether screening is open
     * @param expectedSubjectSize the expected number of subjects
     * @param clinicalNumber      the clinical trial number
     * @param registrationNo      the registration number
     * @param expectStartAt       the expected start date
     * @param expectEndAt         the expected end date
     * @param siteModel           the site model configuration
     * @param enableQuestion      whether questions are enabled
     * @param enableRandomGroup   whether random grouping is enabled
     * @param enableBlind         whether blinding is enabled
     * @param purpose             the trial purpose
     * @param remarks             remarks
     * @param createUserId        the user who created the project
     * @param gmtCreate           the creation timestamp
     * @param gmtModified         the last modification timestamp
     * @param stages              the list of stages
     * @param sitePersonnel       the site personnel assignments
     * @param adverseEventRules   the adverse event rules
     * @param visitPlans          the visit plans
     * @param crfBindings         the CRF bindings
     * @return a fully initialized Project
     */
    public static Project reconstruct(ProjectId id, String title, ProjectType type,
                                       ProjectStatus status, String abbreviation,
                                       String prefix, boolean openScreen,
                                       Integer expectedSubjectSize, String clinicalNumber,
                                       String registrationNo, Date expectStartAt,
                                       Date expectEndAt, String siteModel,
                                       boolean enableQuestion, boolean enableRandomGroup,
                                       boolean enableBlind, String purpose, String remarks,
                                       String createUserId, Date gmtCreate, Date gmtModified,
                                       List<Stage> stages, List<SitePersonnel> sitePersonnel,
                                       List<AdverseEventRule> adverseEventRules,
                                       List<VisitPlan> visitPlans,
                                       List<StageCrfBinding> crfBindings) {
        if (id == null) {
            throw new IllegalArgumentException("Project id must not be null for reconstitution");
        }

        Project project = new Project();
        project.id = id;
        project.title = title;
        project.type = type;
        project.status = status;
        project.abbreviation = abbreviation;
        project.prefix = prefix;
        project.openScreen = openScreen;
        project.expectedSubjectSize = expectedSubjectSize;
        project.clinicalNumber = clinicalNumber;
        project.registrationNo = registrationNo;
        project.expectStartAt = expectStartAt;
        project.expectEndAt = expectEndAt;
        project.siteModel = siteModel;
        project.enableQuestion = enableQuestion;
        project.enableRandomGroup = enableRandomGroup;
        project.enableBlind = enableBlind;
        project.purpose = purpose;
        project.remarks = remarks;
        project.createUserId = createUserId;
        project.gmtCreate = gmtCreate;
        project.gmtModified = gmtModified;
        project.stages = stages != null ? new ArrayList<>(stages) : new ArrayList<>();
        project.sitePersonnel = sitePersonnel != null ? new ArrayList<>(sitePersonnel) : new ArrayList<>();
        project.adverseEventRules = adverseEventRules != null ? new ArrayList<>(adverseEventRules) : new ArrayList<>();
        project.visitPlans = visitPlans != null ? new ArrayList<>(visitPlans) : new ArrayList<>();
        project.crfBindings = crfBindings != null ? new ArrayList<>(crfBindings) : new ArrayList<>();
        return project;
    }

    // ---------------------------------------------------------------
    // Internal helpers
    // ---------------------------------------------------------------

    /**
     * Asserts that the project is in DRAFT status.
     *
     * @throws BusinessRuleViolationException if the project is not in DRAFT status
     */
    private void assertDraftStatus() {
        if (status != ProjectStatus.DRAFT) {
            throw new BusinessRuleViolationException("PROJECT_NOT_DRAFT",
                    "Configuration changes are only allowed in DRAFT status. Current status: " + status);
        }
    }

    // ---------------------------------------------------------------
    // Getters
    // ---------------------------------------------------------------

    @Override
    public ProjectId getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public ProjectType getType() {
        return type;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isOpenScreen() {
        return openScreen;
    }

    public Integer getExpectedSubjectSize() {
        return expectedSubjectSize;
    }

    public String getClinicalNumber() {
        return clinicalNumber;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public Date getExpectStartAt() {
        return expectStartAt;
    }

    public Date getExpectEndAt() {
        return expectEndAt;
    }

    public String getSiteModel() {
        return siteModel;
    }

    public boolean isEnableQuestion() {
        return enableQuestion;
    }

    public boolean isEnableRandomGroup() {
        return enableRandomGroup;
    }

    public boolean isEnableBlind() {
        return enableBlind;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public List<Stage> getStages() {
        return stages != null ? Collections.unmodifiableList(stages) : Collections.emptyList();
    }

    public List<SitePersonnel> getSitePersonnel() {
        return sitePersonnel != null ? Collections.unmodifiableList(sitePersonnel) : Collections.emptyList();
    }

    public List<AdverseEventRule> getAdverseEventRules() {
        return adverseEventRules != null ? Collections.unmodifiableList(adverseEventRules) : Collections.emptyList();
    }

    public List<VisitPlan> getVisitPlans() {
        return visitPlans != null ? Collections.unmodifiableList(visitPlans) : Collections.emptyList();
    }

    public List<StageCrfBinding> getCrfBindings() {
        return crfBindings != null ? Collections.unmodifiableList(crfBindings) : Collections.emptyList();
    }

    // ---------------------------------------------------------------
    // Setters for persistence frameworks
    // ---------------------------------------------------------------

    public void setId(ProjectId id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setOpenScreen(boolean openScreen) {
        this.openScreen = openScreen;
    }

    public void setExpectedSubjectSize(Integer expectedSubjectSize) {
        this.expectedSubjectSize = expectedSubjectSize;
    }

    public void setClinicalNumber(String clinicalNumber) {
        this.clinicalNumber = clinicalNumber;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public void setExpectStartAt(Date expectStartAt) {
        this.expectStartAt = expectStartAt;
    }

    public void setExpectEndAt(Date expectEndAt) {
        this.expectEndAt = expectEndAt;
    }

    public void setSiteModel(String siteModel) {
        this.siteModel = siteModel;
    }

    public void setEnableQuestion(boolean enableQuestion) {
        this.enableQuestion = enableQuestion;
    }

    public void setEnableRandomGroup(boolean enableRandomGroup) {
        this.enableRandomGroup = enableRandomGroup;
    }

    public void setEnableBlind(boolean enableBlind) {
        this.enableBlind = enableBlind;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages != null ? new ArrayList<>(stages) : new ArrayList<>();
    }

    public void setSitePersonnel(List<SitePersonnel> sitePersonnel) {
        this.sitePersonnel = sitePersonnel != null ? new ArrayList<>(sitePersonnel) : new ArrayList<>();
    }

    public void setAdverseEventRules(List<AdverseEventRule> adverseEventRules) {
        this.adverseEventRules = adverseEventRules != null ? new ArrayList<>(adverseEventRules) : new ArrayList<>();
    }

    public void setVisitPlans(List<VisitPlan> visitPlans) {
        this.visitPlans = visitPlans != null ? new ArrayList<>(visitPlans) : new ArrayList<>();
    }

    public void setCrfBindings(List<StageCrfBinding> crfBindings) {
        this.crfBindings = crfBindings != null ? new ArrayList<>(crfBindings) : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Project{id=" + id + ", title='" + title + "', status=" + status + '}';
    }
}
