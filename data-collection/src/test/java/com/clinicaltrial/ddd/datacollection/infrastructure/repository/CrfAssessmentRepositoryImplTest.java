package com.clinicaltrial.ddd.datacollection.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.MonitoringStatus;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.AssessmentScoreJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CompletenessJpa;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CrfAssessmentJpaEntity;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CrfAssessmentSpringDataRepo;
import com.clinicaltrial.ddd.datacollection.infrastructure.persistence.CrfFieldValueJpaEntity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CrfAssessmentRepositoryImpl.class)
class CrfAssessmentRepositoryImplTest {

    @Autowired
    private CrfAssessmentSpringDataRepo springRepo;

    @Autowired
    private CrfAssessmentRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        CrfAssessmentJpaEntity entity = new CrfAssessmentJpaEntity();
        entity.setId(1L);
        entity.setSubjectsUserId(100L);
        entity.setCrfId(200L);
        entity.setCrfVersionId(201L);
        entity.setSubjectsStageId(300L);
        entity.setStatus(MonitoringStatus.COMPLETED);

        CompletenessJpa comp = new CompletenessJpa(new BigDecimal("85.50"), 17, 20);
        entity.setCompleteness(comp);

        entity.setAdverseEvent(true);

        AssessmentScoreJpa score = new AssessmentScoreJpa("A", "PASS");
        entity.setAssessmentScore(score);

        springRepo.save(entity);
        em.flush();
        em.clear();

        CrfAssessment result = repo.getById(new CrfAssessmentId(1L));

        assertThat(result.getId().getValue()).isEqualTo(1L);
        assertThat(result.getSubjectsUserId().getValue()).isEqualTo(100L);
        assertThat(result.getCrfId().getValue()).isEqualTo(200L);
        assertThat(result.getCrfVersionId().getValue()).isEqualTo(201L);
        assertThat(result.getSubjectsStageId().getValue()).isEqualTo(300L);
        assertThat(result.getStatus()).isEqualTo(MonitoringStatus.COMPLETED);

        assertThat(result.getCompleteness()).isNotNull();
        assertThat(result.getCompleteness().getPercentage()).isEqualByComparingTo(new BigDecimal("85.50"));
        assertThat(result.getCompleteness().getFilledCount()).isEqualTo(17);
        assertThat(result.getCompleteness().getTotalCount()).isEqualTo(20);

        assertThat(result.isAdverseEvent()).isTrue();

        assertThat(result.getAssessmentScore()).isNotNull();
        assertThat(result.getAssessmentScore().getScore()).isEqualTo("A");
        assertThat(result.getAssessmentScore().getResult()).isEqualTo("PASS");
    }

    @Test
    void saveWithFieldValues() {
        CrfAssessmentJpaEntity entity = new CrfAssessmentJpaEntity();
        entity.setId(2L);
        entity.setSubjectsUserId(101L);
        entity.setCrfId(201L);
        entity.setSubjectsStageId(301L);
        entity.setStatus(MonitoringStatus.IN_PROGRESS);

        CrfFieldValueJpaEntity fv1 = new CrfFieldValueJpaEntity();
        fv1.setId(21L);
        fv1.setAssessmentId(2L);
        fv1.setFieldCode("HT");
        fv1.setFieldLabel("Height");
        fv1.setFieldValue("175");
        fv1.setFieldValueText("175 cm");
        fv1.setDataUnit("cm");
        fv1.setFieldType("NUMERIC");
        fv1.setSortNumber(1);

        CrfFieldValueJpaEntity fv2 = new CrfFieldValueJpaEntity();
        fv2.setId(22L);
        fv2.setAssessmentId(2L);
        fv2.setFieldCode("WT");
        fv2.setFieldLabel("Weight");
        fv2.setFieldValue("70.5");
        fv2.setFieldValueText("70.5 kg");
        fv2.setDataUnit("kg");
        fv2.setFieldType("DECIMAL");
        fv2.setSubTableId("VITAL_SIGNS");
        fv2.setSortNumber(2);

        entity.getFieldValues().add(fv1);
        entity.getFieldValues().add(fv2);

        springRepo.save(entity);
        em.flush();
        em.clear();

        CrfAssessment result = repo.getById(new CrfAssessmentId(2L));

        assertThat(result.getFieldValues()).hasSize(2);

        assertThat(result.getFieldValues().get(0).getId().getValue()).isEqualTo(21L);
        assertThat(result.getFieldValues().get(0).getFieldCode()).isEqualTo("HT");
        assertThat(result.getFieldValues().get(0).getFieldLabel()).isEqualTo("Height");
        assertThat(result.getFieldValues().get(0).getFieldValue()).isEqualTo("175");
        assertThat(result.getFieldValues().get(0).getFieldValueText()).isEqualTo("175 cm");
        assertThat(result.getFieldValues().get(0).getDataUnit()).isEqualTo("cm");
        assertThat(result.getFieldValues().get(0).getFieldType()).isEqualTo("NUMERIC");
        assertThat(result.getFieldValues().get(0).getSubTableId()).isNull();
        assertThat(result.getFieldValues().get(0).getSortNumber()).isEqualTo(1);

        assertThat(result.getFieldValues().get(1).getId().getValue()).isEqualTo(22L);
        assertThat(result.getFieldValues().get(1).getFieldCode()).isEqualTo("WT");
        assertThat(result.getFieldValues().get(1).getFieldLabel()).isEqualTo("Weight");
        assertThat(result.getFieldValues().get(1).getFieldValue()).isEqualTo("70.5");
        assertThat(result.getFieldValues().get(1).getFieldValueText()).isEqualTo("70.5 kg");
        assertThat(result.getFieldValues().get(1).getDataUnit()).isEqualTo("kg");
        assertThat(result.getFieldValues().get(1).getFieldType()).isEqualTo("DECIMAL");
        assertThat(result.getFieldValues().get(1).getSubTableId()).isEqualTo("VITAL_SIGNS");
        assertThat(result.getFieldValues().get(1).getSortNumber()).isEqualTo(2);
    }

    @Test
    void getByIdNotFound() {
        assertThrows(AggregateNotFoundException.class,
                () -> repo.getById(new CrfAssessmentId(9999L)));
    }

    @Test
    void findBySubjectsStageId() {
        CrfAssessmentJpaEntity a1 = new CrfAssessmentJpaEntity();
        a1.setId(10L);
        a1.setSubjectsUserId(100L);
        a1.setCrfId(1L);
        a1.setSubjectsStageId(500L);
        a1.setStatus(MonitoringStatus.PENDING);

        CrfAssessmentJpaEntity a2 = new CrfAssessmentJpaEntity();
        a2.setId(11L);
        a2.setSubjectsUserId(100L);
        a2.setCrfId(2L);
        a2.setSubjectsStageId(500L);
        a2.setStatus(MonitoringStatus.COMPLETED);

        CrfAssessmentJpaEntity a3 = new CrfAssessmentJpaEntity();
        a3.setId(12L);
        a3.setSubjectsUserId(200L);
        a3.setCrfId(3L);
        a3.setSubjectsStageId(600L);
        a3.setStatus(MonitoringStatus.PENDING);

        springRepo.save(a1);
        springRepo.save(a2);
        springRepo.save(a3);
        em.flush();
        em.clear();

        List<CrfAssessment> results = repo.findBySubjectsStageId(new SubjectStageId(500L));

        assertThat(results).hasSize(2);
        assertThat(results).extracting(a -> a.getId().getValue())
                .containsExactlyInAnyOrder(10L, 11L);
    }
}
