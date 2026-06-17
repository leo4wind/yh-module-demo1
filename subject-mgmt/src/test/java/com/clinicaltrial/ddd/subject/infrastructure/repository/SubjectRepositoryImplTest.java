package com.clinicaltrial.ddd.subject.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.subject.domain.model.aggregate.Subject;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectCode;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectStatus;
import com.clinicaltrial.ddd.subject.domain.repository.SubjectRepository;
import com.clinicaltrial.ddd.subject.infrastructure.persistence.ScreeningInfoJpa;
import com.clinicaltrial.ddd.subject.infrastructure.persistence.SubjectJpaEntity;
import com.clinicaltrial.ddd.subject.infrastructure.persistence.SubjectSpringDataRepo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(SubjectRepositoryImpl.class)
class SubjectRepositoryImplTest {

    @Autowired
    private SubjectSpringDataRepo springRepo;

    @Autowired
    private SubjectRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        SubjectJpaEntity jpa = new SubjectJpaEntity();
        jpa.setId(1L);
        jpa.setProjectId(100L);
        jpa.setCode("TRIAL-0001");
        jpa.setStatus(SubjectStatus.ENROLLED);
        jpa.setUserId(10L);
        jpa.setSiteId(20L);
        jpa.setBlh("BLH001");
        jpa.setSyxh("SYXH001");

        ScreeningInfoJpa screening = new ScreeningInfoJpa();
        screening.setScreeningDate(LocalDate.of(2026, 1, 15));
        screening.setScreeningResult("PASS");
        screening.setRemarks("All eligibility criteria met");
        jpa.setScreeningInfo(screening);

        jpa.setRemarks("Test subject");
        jpa.setTrackDownId(100L);
        jpa.setSupervisorId(200L);

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Subject result = repo.getById(new SubjectId(1L));

        assertThat(result.getId().getValue()).isEqualTo(1L);
        assertThat(result.getProjectId().getValue()).isEqualTo(100L);
        assertThat(result.getCode()).isNotNull();
        assertThat(result.getCode().getFullCode()).isEqualTo("TRIAL-0001");
        assertThat(result.getCode().getProjectPrefix()).isEqualTo("TRIAL");
        assertThat(result.getCode().getSequenceNumber()).isEqualTo(1);
        assertThat(result.getStatus()).isEqualTo(SubjectStatus.ENROLLED);
        assertThat(result.getBlh()).isEqualTo("BLH001");
        assertThat(result.getSyxh()).isEqualTo("SYXH001");
        assertThat(result.getScreeningInfo()).isNotNull();
        assertThat(result.getScreeningInfo().getScreeningDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        assertThat(result.getScreeningInfo().getScreeningResult().name()).isEqualTo("PASS");
    }

    @Test
    void saveWithGroupSubsetIds() {
        SubjectJpaEntity jpa = new SubjectJpaEntity();
        jpa.setId(2L);
        jpa.setProjectId(100L);
        jpa.setCode("TRIAL-0002");
        jpa.setStatus(SubjectStatus.ENROLLED);
        jpa.setUserId(11L);
        jpa.setSiteId(21L);
        jpa.setBlh("BLH002");
        jpa.setSyxh("SYXH002");
        jpa.setGroupSubsetIds(Arrays.asList("GROUP_A", "GROUP_B", "SUBSET_01"));

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Subject result = repo.getById(new SubjectId(2L));

        assertThat(result.getGroupSubsetIds()).containsExactly("GROUP_A", "GROUP_B", "SUBSET_01");
    }

    @Test
    void getByIdNotFound() {
        assertThrows(AggregateNotFoundException.class,
                () -> repo.getById(new SubjectId(9999L)));
    }

    @Test
    void findByProjectId() {
        SubjectJpaEntity s1 = new SubjectJpaEntity();
        s1.setId(10L);
        s1.setProjectId(200L);
        s1.setCode("PROJ-A-0001");
        s1.setStatus(SubjectStatus.ENROLLED);
        s1.setUserId(1L);
        s1.setSiteId(1L);

        SubjectJpaEntity s2 = new SubjectJpaEntity();
        s2.setId(11L);
        s2.setProjectId(200L);
        s2.setCode("PROJ-A-0002");
        s2.setStatus(SubjectStatus.ACTIVE);
        s2.setUserId(2L);
        s2.setSiteId(1L);

        SubjectJpaEntity s3 = new SubjectJpaEntity();
        s3.setId(12L);
        s3.setProjectId(300L);
        s3.setCode("PROJ-B-0001");
        s3.setStatus(SubjectStatus.COMPLETED);
        s3.setUserId(3L);
        s3.setSiteId(2L);

        springRepo.save(s1);
        springRepo.save(s2);
        springRepo.save(s3);
        em.flush();
        em.clear();

        List<Subject> results = repo.findByProjectId(new ProjectId(200L));

        assertThat(results).hasSize(2);
        assertThat(results).extracting(s -> s.getCode().getFullCode())
                .containsExactlyInAnyOrder("PROJ-A-0001", "PROJ-A-0002");
    }

    @Test
    void findByStatus() {
        SubjectJpaEntity enrolled = new SubjectJpaEntity();
        enrolled.setId(20L);
        enrolled.setProjectId(400L);
        enrolled.setCode("ST-0020");
        enrolled.setStatus(SubjectStatus.ENROLLED);
        enrolled.setUserId(1L);
        enrolled.setSiteId(1L);

        SubjectJpaEntity active = new SubjectJpaEntity();
        active.setId(21L);
        active.setProjectId(400L);
        active.setCode("ST-0021");
        active.setStatus(SubjectStatus.ACTIVE);
        active.setUserId(2L);
        active.setSiteId(1L);

        SubjectJpaEntity completed = new SubjectJpaEntity();
        completed.setId(22L);
        completed.setProjectId(400L);
        completed.setCode("ST-0022");
        completed.setStatus(SubjectStatus.COMPLETED);
        completed.setUserId(3L);
        completed.setSiteId(1L);

        springRepo.save(enrolled);
        springRepo.save(active);
        springRepo.save(completed);
        em.flush();
        em.clear();

        List<Subject> enrolledResults = repo.findByStatus(SubjectStatus.ENROLLED);
        assertThat(enrolledResults).hasSize(1);
        assertThat(enrolledResults.get(0).getCode().getFullCode()).isEqualTo("ST-0020");

        List<Subject> activeResults = repo.findByStatus(SubjectStatus.ACTIVE);
        assertThat(activeResults).hasSize(1);
        assertThat(activeResults.get(0).getCode().getFullCode()).isEqualTo("ST-0021");

        List<Subject> completedResults = repo.findByStatus(SubjectStatus.COMPLETED);
        assertThat(completedResults).hasSize(1);
        assertThat(completedResults.get(0).getCode().getFullCode()).isEqualTo("ST-0022");
    }

    @Test
    void saveUpdatesExisting() {
        SubjectJpaEntity jpa = new SubjectJpaEntity();
        jpa.setId(30L);
        jpa.setProjectId(500L);
        jpa.setCode("UPD-0030");
        jpa.setStatus(SubjectStatus.ENROLLED);
        jpa.setUserId(5L);
        jpa.setSiteId(5L);

        springRepo.save(jpa);
        em.flush();
        em.clear();

        Subject loaded = repo.getById(new SubjectId(30L));
        assertThat(loaded.getStatus()).isEqualTo(SubjectStatus.ENROLLED);

        loaded.setRemarks("Updated remarks");
        loaded.setTrackDownId(999L);
        repo.save(loaded);
        em.flush();
        em.clear();

        Subject reloaded = repo.getById(new SubjectId(30L));
        assertThat(reloaded.getRemarks()).isEqualTo("Updated remarks");
        assertThat(reloaded.getTrackDownId()).isEqualTo(999L);
    }
}
