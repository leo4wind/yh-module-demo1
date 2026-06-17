package com.clinicaltrial.ddd.trial.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.trial.domain.model.aggregate.Project;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.AdverseJudgeType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectStatus;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectType;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.SiteRole;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageRepeatType;
import com.clinicaltrial.ddd.trial.domain.repository.ProjectRepository;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.AdverseEventRuleJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.ProjectJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.ProjectSpringDataRepo;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.SitePersonnelJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.StageCrfBindingJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.StageJpaEntity;
import com.clinicaltrial.ddd.trial.infrastructure.persistence.VisitPlanJpaEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ProjectRepositoryImpl.class)
class ProjectRepositoryImplTest {

    @Autowired
    private ProjectSpringDataRepo springRepo;

    @Autowired
    private ProjectRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        ProjectJpaEntity jpa = new ProjectJpaEntity();
        jpa.setId(1L);
        jpa.setTitle("Test Project");
        jpa.setType(ProjectType.INTERVENTIONAL);
        jpa.setStatus(ProjectStatus.DRAFT);
        jpa.setPrefix("TEST");
        jpa.setAbbreviation("TP");
        jpa.setOpenScreen(true);
        jpa.setCreateUserId("user1");
        jpa.setGmtCreate(new Date());
        jpa.setGmtModified(new Date());

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Project result = repo.getById(new ProjectId(1L));

        assertThat(result.getTitle()).isEqualTo("Test Project");
        assertThat(result.getType()).isEqualTo(ProjectType.INTERVENTIONAL);
        assertThat(result.getStatus()).isEqualTo(ProjectStatus.DRAFT);
        assertThat(result.getPrefix()).isEqualTo("TEST");
        assertThat(result.getAbbreviation()).isEqualTo("TP");
    }

    @Test
    void saveWithStages() {
        ProjectJpaEntity jpa = new ProjectJpaEntity();
        jpa.setId(2L);
        jpa.setTitle("Stage Test");
        jpa.setType(ProjectType.OBSERVATIONAL);
        jpa.setStatus(ProjectStatus.DRAFT);
        jpa.setPrefix("STG");
        jpa.setOpenScreen(false);
        jpa.setCreateUserId("user1");
        jpa.setGmtCreate(new Date());
        jpa.setGmtModified(new Date());

        StageJpaEntity stage1 = new StageJpaEntity();
        stage1.setId(21L);
        stage1.setProjectId(2L);
        stage1.setName("Screening");
        stage1.setRepeatType(StageRepeatType.NONE);
        stage1.setAutoAdd(true);
        stage1.setValid(true);

        StageJpaEntity stage2 = new StageJpaEntity();
        stage2.setId(22L);
        stage2.setProjectId(2L);
        stage2.setName("Follow-up");
        stage2.setRepeatType(StageRepeatType.WEEK);
        stage2.setAutoAdd(false);
        stage2.setValid(true);

        jpa.getStages().add(stage1);
        jpa.getStages().add(stage2);

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Project result = repo.getById(new ProjectId(2L));

        assertThat(result.getStages()).hasSize(2);
        assertThat(result.getStages().get(0).getName()).isEqualTo("Screening");
        assertThat(result.getStages().get(0).getRepeatType()).isEqualTo(StageRepeatType.NONE);
        assertThat(result.getStages().get(0).isAutoAdd()).isTrue();
        assertThat(result.getStages().get(1).getName()).isEqualTo("Follow-up");
        assertThat(result.getStages().get(1).getRepeatType()).isEqualTo(StageRepeatType.WEEK);
        assertThat(result.getStages().get(1).isAutoAdd()).isFalse();
    }

    @Test
    void saveWithVisitPlans() {
        ProjectJpaEntity jpa = new ProjectJpaEntity();
        jpa.setId(3L);
        jpa.setTitle("Visit Plan Test");
        jpa.setType(ProjectType.INTERVENTIONAL);
        jpa.setStatus(ProjectStatus.DRAFT);
        jpa.setPrefix("VIS");
        jpa.setOpenScreen(true);
        jpa.setCreateUserId("user1");
        jpa.setGmtCreate(new Date());
        jpa.setGmtModified(new Date());

        VisitPlanJpaEntity plan1 = new VisitPlanJpaEntity();
        plan1.setId(31L);
        plan1.setProjectId(3L);
        plan1.setName("Baseline Visit");
        plan1.setSourceStageId(21L);
        plan1.setTargetStageId(22L);

        VisitPlanJpaEntity plan2 = new VisitPlanJpaEntity();
        plan2.setId(32L);
        plan2.setProjectId(3L);
        plan2.setName("Follow-up Visit");
        plan2.setSourceStageId(22L);
        plan2.setTargetStageId(23L);

        jpa.getVisitPlans().add(plan1);
        jpa.getVisitPlans().add(plan2);

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Project result = repo.getById(new ProjectId(3L));

        assertThat(result.getVisitPlans()).hasSize(2);
        assertThat(result.getVisitPlans().get(0).getName()).isEqualTo("Baseline Visit");
        assertThat(result.getVisitPlans().get(0).getSourceStageId().getValue()).isEqualTo(21L);
        assertThat(result.getVisitPlans().get(0).getTargetStageId().getValue()).isEqualTo(22L);
        assertThat(result.getVisitPlans().get(1).getName()).isEqualTo("Follow-up Visit");
        assertThat(result.getVisitPlans().get(1).getSourceStageId().getValue()).isEqualTo(22L);
        assertThat(result.getVisitPlans().get(1).getTargetStageId().getValue()).isEqualTo(23L);
    }

    @Test
    void saveWithCrfBindings() {
        ProjectJpaEntity jpa = new ProjectJpaEntity();
        jpa.setId(4L);
        jpa.setTitle("CRF Binding Test");
        jpa.setType(ProjectType.OBSERVATIONAL);
        jpa.setStatus(ProjectStatus.DRAFT);
        jpa.setPrefix("CRF");
        jpa.setOpenScreen(true);
        jpa.setCreateUserId("user1");
        jpa.setGmtCreate(new Date());
        jpa.setGmtModified(new Date());

        StageCrfBindingJpaEntity binding = new StageCrfBindingJpaEntity();
        binding.setId(41L);
        binding.setProjectId(4L);
        binding.setStageId(21L);
        binding.setCrfId(100L);
        binding.setCrfVersionId(101L);
        binding.setUserInputEnabled(true);

        jpa.getCrfBindings().add(binding);

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Project result = repo.getById(new ProjectId(4L));

        assertThat(result.getCrfBindings()).hasSize(1);
        assertThat(result.getCrfBindings().get(0).getStageId().getValue()).isEqualTo(21L);
        assertThat(result.getCrfBindings().get(0).getCrfId().getValue()).isEqualTo(100L);
        assertThat(result.getCrfBindings().get(0).isUserInputEnabled()).isTrue();
    }

    @Test
    void saveWithAdverseRules() {
        ProjectJpaEntity jpa = new ProjectJpaEntity();
        jpa.setId(5L);
        jpa.setTitle("AE Rule Test");
        jpa.setType(ProjectType.INTERVENTIONAL);
        jpa.setStatus(ProjectStatus.DRAFT);
        jpa.setPrefix("AE");
        jpa.setOpenScreen(true);
        jpa.setCreateUserId("user1");
        jpa.setGmtCreate(new Date());
        jpa.setGmtModified(new Date());

        AdverseEventRuleJpaEntity rule = new AdverseEventRuleJpaEntity();
        rule.setId(51L);
        rule.setProjectId(5L);
        rule.setStageId(21L);
        rule.setCrfId(100L);
        rule.setJudgeType(AdverseJudgeType.FIELD_VALUE);
        rule.setFieldCode("AE_FIELD");
        rule.setFieldName("Adverse Event Field");
        rule.setValueCode("AE_VAL");
        rule.setValueName("Adverse Event Value");

        jpa.getAdverseEventRules().add(rule);

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Project result = repo.getById(new ProjectId(5L));

        assertThat(result.getAdverseEventRules()).hasSize(1);
        assertThat(result.getAdverseEventRules().get(0).getJudgeType()).isEqualTo(AdverseJudgeType.FIELD_VALUE);
        assertThat(result.getAdverseEventRules().get(0).getFieldCode()).isEqualTo("AE_FIELD");
    }

    @Test
    void saveWithSitePersonnel() {
        ProjectJpaEntity jpa = new ProjectJpaEntity();
        jpa.setId(6L);
        jpa.setTitle("Personnel Test");
        jpa.setType(ProjectType.INTERVENTIONAL);
        jpa.setStatus(ProjectStatus.DRAFT);
        jpa.setPrefix("PER");
        jpa.setOpenScreen(true);
        jpa.setCreateUserId("user1");
        jpa.setGmtCreate(new Date());
        jpa.setGmtModified(new Date());

        SitePersonnelJpaEntity personnel = new SitePersonnelJpaEntity();
        personnel.setId(61L);
        personnel.setProjectId(6L);
        personnel.setUserId(1001L);
        personnel.setSiteId(2001L);
        personnel.setRole(SiteRole.PI);
        personnel.setDisabled(false);

        jpa.getSitePersonnel().add(personnel);

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Project result = repo.getById(new ProjectId(6L));

        assertThat(result.getSitePersonnel()).hasSize(1);
        assertThat(result.getSitePersonnel().get(0).getUserId()).isEqualTo(1001L);
        assertThat(result.getSitePersonnel().get(0).getSiteId()).isEqualTo(2001L);
        assertThat(result.getSitePersonnel().get(0).getRole()).isEqualTo(SiteRole.PI);
    }

    @Test
    void getByIdNotFound() {
        assertThrows(AggregateNotFoundException.class,
                () -> repo.getById(new ProjectId(9999L)));
    }

    @Test
    void findByStatus() {
        ProjectJpaEntity draft1 = new ProjectJpaEntity();
        draft1.setId(10L);
        draft1.setTitle("Draft One");
        draft1.setType(ProjectType.INTERVENTIONAL);
        draft1.setStatus(ProjectStatus.DRAFT);
        draft1.setPrefix("DR1");
        draft1.setOpenScreen(true);
        draft1.setCreateUserId("user1");
        draft1.setGmtCreate(new Date());
        draft1.setGmtModified(new Date());

        ProjectJpaEntity draft2 = new ProjectJpaEntity();
        draft2.setId(11L);
        draft2.setTitle("Draft Two");
        draft2.setType(ProjectType.OBSERVATIONAL);
        draft2.setStatus(ProjectStatus.DRAFT);
        draft2.setPrefix("DR2");
        draft2.setOpenScreen(false);
        draft2.setCreateUserId("user2");
        draft2.setGmtCreate(new Date());
        draft2.setGmtModified(new Date());

        ProjectJpaEntity active = new ProjectJpaEntity();
        active.setId(12L);
        active.setTitle("Active One");
        active.setType(ProjectType.INTERVENTIONAL);
        active.setStatus(ProjectStatus.ACTIVE);
        active.setPrefix("ACT");
        active.setOpenScreen(true);
        active.setCreateUserId("user3");
        active.setGmtCreate(new Date());
        active.setGmtModified(new Date());

        springRepo.save(draft1);
        springRepo.save(draft2);
        springRepo.save(active);
        em.flush();
        em.clear();

        java.util.List<Project> drafts = repo.findByStatus(ProjectStatus.DRAFT);

        assertThat(drafts).hasSize(2);
        assertThat(drafts).extracting(Project::getTitle)
                .containsExactlyInAnyOrder("Draft One", "Draft Two");
    }

    @Test
    void saveUpdatesExisting() {
        ProjectJpaEntity jpa = new ProjectJpaEntity();
        jpa.setId(20L);
        jpa.setTitle("Original Title");
        jpa.setType(ProjectType.INTERVENTIONAL);
        jpa.setStatus(ProjectStatus.DRAFT);
        jpa.setPrefix("UPD");
        jpa.setOpenScreen(true);
        jpa.setCreateUserId("user1");
        jpa.setGmtCreate(new Date());
        jpa.setGmtModified(new Date());

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Project loaded = repo.getById(new ProjectId(20L));
        assertThat(loaded.getTitle()).isEqualTo("Original Title");

        loaded.setTitle("Updated Title");
        repo.save(loaded);
        em.flush();
        em.clear();

        Project reloaded = repo.getById(new ProjectId(20L));
        assertThat(reloaded.getTitle()).isEqualTo("Updated Title");
    }
}
