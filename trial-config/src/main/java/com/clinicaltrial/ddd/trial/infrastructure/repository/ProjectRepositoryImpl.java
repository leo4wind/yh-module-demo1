package com.clinicaltrial.ddd.trial.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
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
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SitePersonnelId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageCrfBindingId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.WindowPeriod;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.AdverseEventRuleJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.BaselineIntervalJpa;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.ProjectJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.ProjectSpringDataRepo;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.SitePersonnelJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.StageCrfBindingJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.StageJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.VisitPlanJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.WindowPeriodJpa;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for {@link Project} aggregate.
 * Maps between domain Project aggregate and JPA entities.
 */
@Repository
@Transactional
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectSpringDataRepo springDataRepo;

    public ProjectRepositoryImpl(ProjectSpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Project> findById(ProjectId id) {
        return springDataRepo.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Project getById(ProjectId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("Project", id.getValue()));
    }

    @Override
    @Transactional
    public Project save(Project project) {
        ProjectJpaEntity entity = toJpa(project);
        ProjectJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> findByStatus(ProjectStatus status) {
        return springDataRepo.findByStatus(status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> findAll(int page, int size) {
        return springDataRepo.findAll(PageRequest.of(page, size))
                .getContent().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByPrefix(String prefix) {
        return springDataRepo.countByPrefix(prefix);
    }

    // ========== Domain -> JPA mapping ==========

    private ProjectJpaEntity toJpa(Project domain) {
        ProjectJpaEntity entity = new ProjectJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setTitle(domain.getTitle());
        entity.setType(domain.getType());
        entity.setStatus(domain.getStatus());
        entity.setAbbreviation(domain.getAbbreviation());
        entity.setPrefix(domain.getPrefix());
        entity.setOpenScreen(domain.isOpenScreen());
        entity.setExpectedSubjectSize(domain.getExpectedSubjectSize());
        entity.setClinicalNumber(domain.getClinicalNumber());
        entity.setRegistrationNo(domain.getRegistrationNo());
        entity.setExpectStartAt(domain.getExpectStartAt());
        entity.setExpectEndAt(domain.getExpectEndAt());
        entity.setSiteModel(domain.getSiteModel());
        entity.setEnableQuestion(domain.isEnableQuestion());
        entity.setEnableRandomGroup(domain.isEnableRandomGroup());
        entity.setEnableBlind(domain.isEnableBlind());
        entity.setPurpose(domain.getPurpose());
        entity.setRemarks(domain.getRemarks());
        entity.setCreateUserId(domain.getCreateUserId());
        entity.setGmtCreate(domain.getGmtCreate());
        entity.setGmtModified(domain.getGmtModified());
        entity.setStages(toJpaStages(domain.getStages(), entity.getId()));
        entity.setSitePersonnel(toJpaSitePersonnel(domain.getSitePersonnel(), entity.getId()));
        entity.setAdverseEventRules(toJpaAdverseEventRules(domain.getAdverseEventRules(), entity.getId()));
        entity.setVisitPlans(toJpaVisitPlans(domain.getVisitPlans(), entity.getId()));
        entity.setCrfBindings(toJpaStageCrfBindings(domain.getCrfBindings(), entity.getId()));
        return entity;
    }

    private List<StageJpaEntity> toJpaStages(List<Stage> stages, Long projectId) {
        if (stages == null) return Collections.emptyList();
        return stages.stream()
                .map(s -> toJpaStage(s, projectId))
                .collect(Collectors.toList());
    }

    private StageJpaEntity toJpaStage(Stage stage, Long projectId) {
        StageJpaEntity entity = new StageJpaEntity();
        entity.setId(stage.getId() != null ? stage.getId().getValue() : null);
        entity.setProjectId(projectId);
        entity.setName(stage.getName());
        entity.setRepeatType(stage.getRepeatType());
        entity.setAutoAdd(stage.isAutoAdd());
        entity.setValid(stage.isValid());
        entity.setBaselineInterval(toJpaBaselineInterval(stage.getBaselineInterval()));
        entity.setWindowPeriod(toJpaWindowPeriod(stage.getWindowPeriod()));
        entity.setTaskFlag(stage.getTaskFlag());
        return entity;
    }

    private List<SitePersonnelJpaEntity> toJpaSitePersonnel(List<SitePersonnel> personnel, Long projectId) {
        if (personnel == null) return Collections.emptyList();
        return personnel.stream()
                .map(p -> toJpaSitePersonnel(p, projectId))
                .collect(Collectors.toList());
    }

    private SitePersonnelJpaEntity toJpaSitePersonnel(SitePersonnel p, Long projectId) {
        SitePersonnelJpaEntity entity = new SitePersonnelJpaEntity();
        entity.setId(p.getId() != null ? p.getId().getValue() : null);
        entity.setProjectId(projectId);
        entity.setUserId(p.getUserId());
        entity.setSiteId(p.getSiteId());
        entity.setRole(p.getRole());
        entity.setDisabled(p.isDisabled());
        return entity;
    }

    private List<AdverseEventRuleJpaEntity> toJpaAdverseEventRules(List<AdverseEventRule> rules, Long projectId) {
        if (rules == null) return Collections.emptyList();
        return rules.stream()
                .map(r -> toJpaAdverseEventRule(r, projectId))
                .collect(Collectors.toList());
    }

    private AdverseEventRuleJpaEntity toJpaAdverseEventRule(AdverseEventRule rule, Long projectId) {
        AdverseEventRuleJpaEntity entity = new AdverseEventRuleJpaEntity();
        entity.setId(rule.getId() != null ? rule.getId().getValue() : null);
        entity.setProjectId(projectId);
        entity.setStageId(rule.getStageId() != null ? rule.getStageId().getValue() : null);
        entity.setCrfId(rule.getCrfId() != null ? rule.getCrfId().getValue() : null);
        entity.setCrfVersionId(rule.getCrfVersionId() != null ? rule.getCrfVersionId().getValue() : null);
        entity.setJudgeType(rule.getJudgeType());
        entity.setFieldCode(rule.getFieldCode());
        entity.setFieldName(rule.getFieldName());
        entity.setValueCode(rule.getValueCode());
        entity.setValueName(rule.getValueName());
        return entity;
    }

    private List<VisitPlanJpaEntity> toJpaVisitPlans(List<VisitPlan> plans, Long projectId) {
        if (plans == null) return Collections.emptyList();
        return plans.stream()
                .map(v -> toJpaVisitPlan(v, projectId))
                .collect(Collectors.toList());
    }

    private VisitPlanJpaEntity toJpaVisitPlan(VisitPlan plan, Long projectId) {
        VisitPlanJpaEntity entity = new VisitPlanJpaEntity();
        entity.setId(plan.getId() != null ? plan.getId().getValue() : null);
        entity.setProjectId(projectId);
        entity.setName(plan.getName());
        entity.setSourceStageId(plan.getSourceStageId() != null ? plan.getSourceStageId().getValue() : null);
        entity.setTargetStageId(plan.getTargetStageId() != null ? plan.getTargetStageId().getValue() : null);
        entity.setBaselineInterval(toJpaBaselineInterval(plan.getBaselineInterval()));
        entity.setWindowPeriod(toJpaWindowPeriod(plan.getWindowPeriod()));
        entity.setCrfComponentId(plan.getCrfComponentId());
        return entity;
    }

    private List<StageCrfBindingJpaEntity> toJpaStageCrfBindings(List<StageCrfBinding> bindings, Long projectId) {
        if (bindings == null) return Collections.emptyList();
        return bindings.stream()
                .map(b -> toJpaStageCrfBinding(b, projectId))
                .collect(Collectors.toList());
    }

    private StageCrfBindingJpaEntity toJpaStageCrfBinding(StageCrfBinding binding, Long projectId) {
        StageCrfBindingJpaEntity entity = new StageCrfBindingJpaEntity();
        entity.setId(binding.getId() != null ? binding.getId().getValue() : null);
        entity.setProjectId(projectId);
        entity.setStageId(binding.getStageId() != null ? binding.getStageId().getValue() : null);
        entity.setCrfId(binding.getCrfId() != null ? binding.getCrfId().getValue() : null);
        entity.setCrfVersionId(binding.getCrfVersionId() != null ? binding.getCrfVersionId().getValue() : null);
        entity.setUserInputEnabled(binding.isUserInputEnabled());
        return entity;
    }

    private BaselineIntervalJpa toJpaBaselineInterval(BaselineInterval bi) {
        if (bi == null) return null;
        return new BaselineIntervalJpa(bi.getInterval(), bi.getUnit());
    }

    private WindowPeriodJpa toJpaWindowPeriod(WindowPeriod wp) {
        if (wp == null) return null;
        return new WindowPeriodJpa(wp.getBeforeDays(), wp.getAfterDays());
    }

    // ========== JPA -> Domain mapping ==========

    private Project toDomain(ProjectJpaEntity entity) {
        return Project.reconstruct(
                new ProjectId(entity.getId()),
                entity.getTitle(),
                entity.getType(),
                entity.getStatus(),
                entity.getAbbreviation(),
                entity.getPrefix(),
                entity.isOpenScreen(),
                entity.getExpectedSubjectSize(),
                entity.getClinicalNumber(),
                entity.getRegistrationNo(),
                entity.getExpectStartAt(),
                entity.getExpectEndAt(),
                entity.getSiteModel(),
                entity.isEnableQuestion(),
                entity.isEnableRandomGroup(),
                entity.isEnableBlind(),
                entity.getPurpose(),
                entity.getRemarks(),
                entity.getCreateUserId(),
                entity.getGmtCreate(),
                entity.getGmtModified(),
                toDomainStages(entity.getStages()),
                toDomainSitePersonnel(entity.getSitePersonnel()),
                toDomainAdverseEventRules(entity.getAdverseEventRules()),
                toDomainVisitPlans(entity.getVisitPlans()),
                toDomainStageCrfBindings(entity.getCrfBindings())
        );
    }

    private List<Stage> toDomainStages(List<StageJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainStage)
                .collect(Collectors.toList());
    }

    private Stage toDomainStage(StageJpaEntity entity) {
        Stage stage = Stage.create(
                new StageId(entity.getId()),
                new ProjectId(entity.getProjectId()),
                entity.getName(),
                entity.getRepeatType(),
                entity.isAutoAdd(),
                toDomainBaselineInterval(entity.getBaselineInterval()),
                toDomainWindowPeriod(entity.getWindowPeriod()),
                entity.getTaskFlag()
        );
        if (!entity.isValid()) {
            stage.markInvalid();
        }
        return stage;
    }

    private List<SitePersonnel> toDomainSitePersonnel(List<SitePersonnelJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainSitePersonnel)
                .collect(Collectors.toList());
    }

    private SitePersonnel toDomainSitePersonnel(SitePersonnelJpaEntity entity) {
        SitePersonnel sp = SitePersonnel.create(
                new SitePersonnelId(entity.getId()),
                new ProjectId(entity.getProjectId()),
                entity.getUserId(),
                entity.getSiteId(),
                entity.getRole()
        );
        if (entity.isDisabled()) {
            sp.disable();
        }
        return sp;
    }

    private List<AdverseEventRule> toDomainAdverseEventRules(List<AdverseEventRuleJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainAdverseEventRule)
                .collect(Collectors.toList());
    }

    private AdverseEventRule toDomainAdverseEventRule(AdverseEventRuleJpaEntity entity) {
        return AdverseEventRule.create(
                new AdverseEventRuleId(entity.getId()),
                new ProjectId(entity.getProjectId()),
                new StageId(entity.getStageId()),
                new CrfTemplateId(entity.getCrfId()),
                entity.getCrfVersionId() != null ? new CrfVersionId(entity.getCrfVersionId()) : null,
                entity.getJudgeType(),
                entity.getFieldCode(),
                entity.getFieldName(),
                entity.getValueCode(),
                entity.getValueName()
        );
    }

    private List<VisitPlan> toDomainVisitPlans(List<VisitPlanJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainVisitPlan)
                .collect(Collectors.toList());
    }

    private VisitPlan toDomainVisitPlan(VisitPlanJpaEntity entity) {
        return VisitPlan.create(
                new VisitPlanId(entity.getId()),
                new ProjectId(entity.getProjectId()),
                entity.getName(),
                new StageId(entity.getSourceStageId()),
                new StageId(entity.getTargetStageId()),
                toDomainBaselineInterval(entity.getBaselineInterval()),
                toDomainWindowPeriod(entity.getWindowPeriod()),
                entity.getCrfComponentId()
        );
    }

    private List<StageCrfBinding> toDomainStageCrfBindings(List<StageCrfBindingJpaEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(this::toDomainStageCrfBinding)
                .collect(Collectors.toList());
    }

    private StageCrfBinding toDomainStageCrfBinding(StageCrfBindingJpaEntity entity) {
        return StageCrfBinding.create(
                new StageCrfBindingId(entity.getId()),
                new ProjectId(entity.getProjectId()),
                new StageId(entity.getStageId()),
                new CrfTemplateId(entity.getCrfId()),
                entity.getCrfVersionId() != null ? new CrfVersionId(entity.getCrfVersionId()) : null,
                entity.isUserInputEnabled()
        );
    }

    private BaselineInterval toDomainBaselineInterval(BaselineIntervalJpa jpa) {
        if (jpa == null) return null;
        return new BaselineInterval(jpa.getInterval(), jpa.getNormalizedUnit());
    }

    private WindowPeriod toDomainWindowPeriod(WindowPeriodJpa jpa) {
        if (jpa == null) return null;
        return new WindowPeriod(jpa.getBeforeDays(), jpa.getAfterDays());
    }
}
