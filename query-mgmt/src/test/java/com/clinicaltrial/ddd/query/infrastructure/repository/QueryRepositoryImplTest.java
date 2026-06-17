package com.clinicaltrial.ddd.query.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryType;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;
import com.clinicaltrial.ddd.query.infrastructure.persistence.QueryJpaEntity;
import com.clinicaltrial.ddd.query.infrastructure.persistence.QuerySpringDataRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryRepositoryImpl.class)
class QueryRepositoryImplTest {

    @Autowired
    private QuerySpringDataRepo springRepo;

    @Autowired
    private QueryRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        Date now = new Date(1700000000000L);

        QueryJpaEntity entity = new QueryJpaEntity();
        entity.setId(1L);
        entity.setAssessmentId(100L);
        entity.setFieldCode("BP_SYSTOLIC");
        entity.setSubTableId("VITAL_SIGNS");
        entity.setFieldType("NUMERIC");
        entity.setStatus(QueryStatus.OPEN);
        entity.setType(QueryType.MONITOR_QUERY);
        entity.setQuestion("Why is BP so high?");
        entity.setResponse(null);
        entity.setUpdateType(null);
        entity.setCreateUserId(10L);
        entity.setUpdateUserId(10L);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        entity.setOriginalFieldCode("BP_SYSTOLIC");
        entity.setOriginalFieldLabel("Systolic BP");
        entity.setOriginalFieldValue("180");
        entity.setOriginalFieldValueText("180 mmHg");
        entity.setOriginalSnapshotAt(now);

        entity.setCurrentFieldCode("BP_SYSTOLIC");
        entity.setCurrentFieldLabel("Systolic BP");
        entity.setCurrentFieldValue("180");
        entity.setCurrentFieldValueText("180 mmHg");
        entity.setCurrentSnapshotAt(now);

        springRepo.save(entity);
        em.flush();
        em.clear();

        Query result = repo.getById(new QueryId(1L));

        assertThat(result.getId().getValue()).isEqualTo(1L);
        assertThat(result.getAssessmentId().getValue()).isEqualTo(100L);

        assertThat(result.getFieldIdentifier()).isNotNull();
        assertThat(result.getFieldIdentifier().getFieldCode()).isEqualTo("BP_SYSTOLIC");
        assertThat(result.getFieldIdentifier().getSubTableId()).isEqualTo("VITAL_SIGNS");
        assertThat(result.getFieldIdentifier().getFieldType()).isEqualTo("NUMERIC");

        assertThat(result.getStatus()).isEqualTo(QueryStatus.OPEN);
        assertThat(result.getType()).isEqualTo(QueryType.MONITOR_QUERY);
        assertThat(result.getQuestion()).isEqualTo("Why is BP so high?");
        assertThat(result.getResponse()).isNull();
        assertThat(result.getUpdateType()).isNull();
        assertThat(result.getCreateUserId()).isEqualTo(10L);
        assertThat(result.getUpdateUserId()).isEqualTo(10L);
        assertThat(result.getCreateTime().getTime()).isEqualTo(now.getTime());
        assertThat(result.getUpdateTime().getTime()).isEqualTo(now.getTime());

        assertThat(result.getOriginalValue()).isNotNull();
        assertThat(result.getOriginalValue().getFieldCode()).isEqualTo("BP_SYSTOLIC");
        assertThat(result.getOriginalValue().getFieldLabel()).isEqualTo("Systolic BP");
        assertThat(result.getOriginalValue().getFieldValue()).isEqualTo("180");
        assertThat(result.getOriginalValue().getFieldValueText()).isEqualTo("180 mmHg");
        assertThat(result.getOriginalValue().getSnapshotAt().getTime()).isEqualTo(now.getTime());

        assertThat(result.getCurrentValue()).isNotNull();
        assertThat(result.getCurrentValue().getFieldCode()).isEqualTo("BP_SYSTOLIC");
        assertThat(result.getCurrentValue().getFieldLabel()).isEqualTo("Systolic BP");
        assertThat(result.getCurrentValue().getFieldValue()).isEqualTo("180");
        assertThat(result.getCurrentValue().getFieldValueText()).isEqualTo("180 mmHg");
        assertThat(result.getCurrentValue().getSnapshotAt().getTime()).isEqualTo(now.getTime());
    }

    @Test
    void getByIdNotFound() {
        assertThrows(AggregateNotFoundException.class,
                () -> repo.getById(new QueryId(9999L)));
    }

    @Test
    void findByAssessmentId() {
        QueryJpaEntity q1 = new QueryJpaEntity();
        q1.setId(10L);
        q1.setAssessmentId(500L);
        q1.setFieldCode("field1");
        q1.setStatus(QueryStatus.OPEN);
        q1.setType(QueryType.MONITOR_QUERY);
        q1.setQuestion("Q1?");
        q1.setCreateUserId(1L);
        q1.setUpdateUserId(1L);
        q1.setCreateTime(new Date());
        q1.setUpdateTime(new Date());

        QueryJpaEntity q2 = new QueryJpaEntity();
        q2.setId(11L);
        q2.setAssessmentId(500L);
        q2.setFieldCode("field2");
        q2.setStatus(QueryStatus.CLOSED);
        q2.setType(QueryType.AUDIT_QUERY);
        q2.setQuestion("Q2?");
        q2.setCreateUserId(2L);
        q2.setUpdateUserId(2L);
        q2.setCreateTime(new Date());
        q2.setUpdateTime(new Date());

        QueryJpaEntity q3 = new QueryJpaEntity();
        q3.setId(12L);
        q3.setAssessmentId(600L);
        q3.setFieldCode("field3");
        q3.setStatus(QueryStatus.OPEN);
        q3.setType(QueryType.MONITOR_QUERY);
        q3.setQuestion("Q3?");
        q3.setCreateUserId(3L);
        q3.setUpdateUserId(3L);
        q3.setCreateTime(new Date());
        q3.setUpdateTime(new Date());

        springRepo.save(q1);
        springRepo.save(q2);
        springRepo.save(q3);
        em.flush();
        em.clear();

        List<Query> results = repo.findByAssessmentId(new CrfAssessmentId(500L));

        assertThat(results).hasSize(2);
        assertThat(results).extracting(q -> q.getId().getValue())
                .containsExactlyInAnyOrder(10L, 11L);
    }

    @Test
    void countOpenQueriesByAssessmentId() {
        QueryJpaEntity open1 = new QueryJpaEntity();
        open1.setId(20L);
        open1.setAssessmentId(1000L);
        open1.setFieldCode("f1");
        open1.setStatus(QueryStatus.OPEN);
        open1.setType(QueryType.MONITOR_QUERY);
        open1.setQuestion("Q?");
        open1.setCreateUserId(1L);
        open1.setUpdateUserId(1L);
        open1.setCreateTime(new Date());
        open1.setUpdateTime(new Date());

        QueryJpaEntity open2 = new QueryJpaEntity();
        open2.setId(21L);
        open2.setAssessmentId(1000L);
        open2.setFieldCode("f2");
        open2.setStatus(QueryStatus.OPEN);
        open2.setType(QueryType.MONITOR_QUERY);
        open2.setQuestion("Q?");
        open2.setCreateUserId(1L);
        open2.setUpdateUserId(1L);
        open2.setCreateTime(new Date());
        open2.setUpdateTime(new Date());

        QueryJpaEntity responded = new QueryJpaEntity();
        responded.setId(22L);
        responded.setAssessmentId(1000L);
        responded.setFieldCode("f3");
        responded.setStatus(QueryStatus.RESPONDED);
        responded.setType(QueryType.MONITOR_QUERY);
        responded.setQuestion("Q?");
        responded.setResponse("A");
        responded.setCreateUserId(1L);
        responded.setUpdateUserId(1L);
        responded.setCreateTime(new Date());
        responded.setUpdateTime(new Date());

        QueryJpaEntity closed = new QueryJpaEntity();
        closed.setId(23L);
        closed.setAssessmentId(1000L);
        closed.setFieldCode("f4");
        closed.setStatus(QueryStatus.CLOSED);
        closed.setType(QueryType.MONITOR_QUERY);
        closed.setQuestion("Q?");
        closed.setResponse("A");
        closed.setCreateUserId(1L);
        closed.setUpdateUserId(1L);
        closed.setCreateTime(new Date());
        closed.setUpdateTime(new Date());

        QueryJpaEntity otherOpen = new QueryJpaEntity();
        otherOpen.setId(24L);
        otherOpen.setAssessmentId(1001L);
        otherOpen.setFieldCode("f5");
        otherOpen.setStatus(QueryStatus.OPEN);
        otherOpen.setType(QueryType.MONITOR_QUERY);
        otherOpen.setQuestion("Q?");
        otherOpen.setCreateUserId(1L);
        otherOpen.setUpdateUserId(1L);
        otherOpen.setCreateTime(new Date());
        otherOpen.setUpdateTime(new Date());

        springRepo.save(open1);
        springRepo.save(open2);
        springRepo.save(responded);
        springRepo.save(closed);
        springRepo.save(otherOpen);
        em.flush();
        em.clear();

        int count = repo.countOpenQueriesByAssessmentId(new CrfAssessmentId(1000L));

        assertThat(count).isEqualTo(3);
    }

    @Test
    void saveUpdatesExisting() {
        Date now = new Date(1700000000000L);

        QueryJpaEntity entity = new QueryJpaEntity();
        entity.setId(30L);
        entity.setAssessmentId(500L);
        entity.setFieldCode("field");
        entity.setStatus(QueryStatus.OPEN);
        entity.setType(QueryType.MONITOR_QUERY);
        entity.setQuestion("Original question?");
        entity.setCreateUserId(10L);
        entity.setUpdateUserId(10L);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        entity.setOriginalFieldCode("field");
        entity.setOriginalFieldValue("old");
        entity.setOriginalSnapshotAt(now);

        springRepo.save(entity);
        em.flush();
        em.clear();

        Query loaded = repo.getById(new QueryId(30L));
        assertThat(loaded.getStatus()).isEqualTo(QueryStatus.OPEN);

        loaded.close(20L);
        assertThat(loaded.getStatus()).isEqualTo(QueryStatus.CLOSED);

        repo.save(loaded);
        em.flush();
        em.clear();

        Query reloaded = repo.getById(new QueryId(30L));
        assertThat(reloaded.getStatus()).isEqualTo(QueryStatus.CLOSED);
        assertThat(reloaded.getUpdateUserId()).isEqualTo(20L);
    }
}
