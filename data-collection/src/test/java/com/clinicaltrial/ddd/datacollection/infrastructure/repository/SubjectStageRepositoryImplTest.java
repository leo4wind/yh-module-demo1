package com.clinicaltrial.ddd.datacollection.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageStatus;
import com.clinicaltrial.ddd.datacollection.domain.repository.SubjectStageRepository;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.BaselineTimeJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.FollowUpPeriodJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.SubjectStageJpaEntity;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.SubjectStageSpringDataRepo;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SubjectStageRepositoryImpl.class)
class SubjectStageRepositoryImplTest {

    @Autowired
    private SubjectStageSpringDataRepo springRepo;

    @Autowired
    private SubjectStageRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        SubjectStageJpaEntity entity = new SubjectStageJpaEntity();
        entity.setId(1L);
        entity.setSubjectsUserId(100L);
        entity.setStageId(200L);
        entity.setPlanEventId(300L);
        entity.setStatus(SubjectStageStatus.IN_PROGRESS);

        Date stageStart = new Date(1700000000000L);
        Date stageEnd = new Date(1700086400000L);
        entity.setStageStartAt(stageStart);
        entity.setStageEndAt(stageEnd);

        BaselineTimeJpa bt = new BaselineTimeJpa(new Date(1699920000000L), "LAB_RESULT");
        entity.setBaselineTime(bt);

        FollowUpPeriodJpa fp = new FollowUpPeriodJpa(stageStart, new Date(1702600000000L));
        entity.setFollowUpPeriod(fp);

        entity.setFollowUpStatus("ONGOING");
        entity.setCompleteTime(new Date(1702600000000L));
        entity.setCompleteUserId(50L);
        entity.setCrfAssessmentRefs(Arrays.asList(10L, 20L, 30L));

        springRepo.save(entity);
        em.flush();
        em.clear();

        SubjectStage result = repo.getById(new SubjectStageId(1L));

        assertThat(result.getId().getValue()).isEqualTo(1L);
        assertThat(result.getSubjectsUserId().getValue()).isEqualTo(100L);
        assertThat(result.getStageId().getValue()).isEqualTo(200L);
        assertThat(result.getPlanEventId().getValue()).isEqualTo(300L);
        assertThat(result.getStatus()).isEqualTo(SubjectStageStatus.IN_PROGRESS);
        assertThat(result.getStageStartAt().getTime()).isEqualTo(stageStart.getTime());
        assertThat(result.getStageEndAt().getTime()).isEqualTo(stageEnd.getTime());

        assertThat(result.getBaselineTime()).isNotNull();
        assertThat(result.getBaselineTime().getSource()).isEqualTo("LAB_RESULT");

        assertThat(result.getFollowUpPeriod()).isNotNull();
        assertThat(result.getFollowUpStatus()).isEqualTo("ONGOING");
        assertThat(result.getCompleteUserId()).isEqualTo(50L);

        assertThat(result.getCrfAssessmentRefs()).hasSize(3);
        assertThat(result.getCrfAssessmentRefs())
                .extracting(ref -> ref.getAssessmentId().getValue())
                .containsExactly(10L, 20L, 30L);
    }

    @Test
    void saveWithCrfAssessmentRefs() {
        SubjectStageJpaEntity entity = new SubjectStageJpaEntity();
        entity.setId(2L);
        entity.setSubjectsUserId(101L);
        entity.setStageId(201L);
        entity.setStatus(SubjectStageStatus.PENDING);
        entity.setCrfAssessmentRefs(Arrays.asList(100L, 200L, 300L, 400L));

        springRepo.save(entity);
        em.flush();
        em.clear();

        SubjectStage result = repo.getById(new SubjectStageId(2L));

        assertThat(result.getCrfAssessmentRefs()).hasSize(4);
        assertThat(result.getCrfAssessmentRefs())
                .extracting(ref -> ref.getAssessmentId().getValue())
                .containsExactly(100L, 200L, 300L, 400L);
    }

    @Test
    void getByIdNotFound() {
        assertThrows(AggregateNotFoundException.class,
                () -> repo.getById(new SubjectStageId(9999L)));
    }

    @Test
    void findBySubjectId() {
        SubjectStageJpaEntity s1 = new SubjectStageJpaEntity();
        s1.setId(10L);
        s1.setSubjectsUserId(100L);
        s1.setStageId(1L);
        s1.setStatus(SubjectStageStatus.COMPLETED);

        SubjectStageJpaEntity s2 = new SubjectStageJpaEntity();
        s2.setId(11L);
        s2.setSubjectsUserId(100L);
        s2.setStageId(2L);
        s2.setStatus(SubjectStageStatus.IN_PROGRESS);

        SubjectStageJpaEntity s3 = new SubjectStageJpaEntity();
        s3.setId(12L);
        s3.setSubjectsUserId(200L);
        s3.setStageId(1L);
        s3.setStatus(SubjectStageStatus.PENDING);

        springRepo.save(s1);
        springRepo.save(s2);
        springRepo.save(s3);
        em.flush();
        em.clear();

        List<SubjectStage> results = repo.findBySubjectId(new SubjectId(100L));

        assertThat(results).hasSize(2);
        assertThat(results).extracting(s -> s.getId().getValue())
                .containsExactlyInAnyOrder(10L, 11L);
    }
}
