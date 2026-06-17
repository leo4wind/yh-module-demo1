package com.clinicaltrial.ddd.dataexport.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportExecutionLog;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFieldConfig;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFilter;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportStatus;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.FileFormat;
import com.clinicaltrial.ddd.dataexport.domain.repository.ExportTaskRepository;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportExecutionLogJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportFieldConfigJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportFilterJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportTaskJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportTaskSpringDataRepo;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(ExportTaskRepositoryImpl.class)
class ExportTaskRepositoryImplTest {

    @Autowired
    private ExportTaskSpringDataRepo springRepo;

    @Autowired
    private ExportTaskRepository repo;

    @Autowired
    private TestEntityManager em;

    @Test
    void saveAndFindById() {
        // Build JPA entity with all fields
        ExportTaskJpaEntity entity = new ExportTaskJpaEntity();
        entity.setId(1L);
        entity.setTaskName("Full Round-Trip Export");
        entity.setProjectId(100L);
        entity.setStageId(200L);
        entity.setCrfVersionId(300L);
        entity.setStatus(ExportStatus.DRAFT);
        entity.setFileFormat(FileFormat.XLSX);
        entity.setAuditUserId("admin");
        entity.setAuditTime(new Date(1700000000000L));
        entity.setAuditMessage("Audit message");
        entity.setFileUrl("/data/exports/file.xlsx");
        entity.setFileName("clinical-data.xlsx");
        entity.setDownloadCount(3);
        entity.setFailCount(1);
        entity.setFailMessage("Previous failure");

        springRepo.save(entity);
        em.flush();
        em.clear();

        // Read via domain repository to exercise JPA -> domain mapping
        ExportTask found = repo.findById(new ExportTaskId(1L)).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId().getValue()).isEqualTo(1L);
        assertThat(found.getTaskName()).isEqualTo("Full Round-Trip Export");
        assertThat(found.getProjectId()).isNotNull();
        assertThat(found.getProjectId().getValue()).isEqualTo(100L);
        assertThat(found.getStageId()).isNotNull();
        assertThat(found.getStageId().getValue()).isEqualTo(200L);
        assertThat(found.getCrfVersionId()).isNotNull();
        assertThat(found.getCrfVersionId().getValue()).isEqualTo(300L);
        assertThat(found.getStatus()).isEqualTo(ExportStatus.DRAFT);
        assertThat(found.getFileFormat()).isEqualTo(FileFormat.XLSX);
        assertThat(found.getAuditUserId()).isEqualTo("admin");
        assertThat(found.getAuditTime()).isNotNull();
        assertThat(found.getAuditMessage()).isEqualTo("Audit message");
        assertThat(found.getFileUrl()).isEqualTo("/data/exports/file.xlsx");
        assertThat(found.getFileName()).isEqualTo("clinical-data.xlsx");
        assertThat(found.getDownloadCount()).isEqualTo(3);
        assertThat(found.getFailCount()).isEqualTo(1);
        assertThat(found.getFailMessage()).isEqualTo("Previous failure");
        assertThat(found.getFieldConfigs()).isEmpty();
        assertThat(found.getFilters()).isEmpty();
        assertThat(found.getExecutionLogs()).isEmpty();
    }

    @Test
    void saveWithFieldConfigs() {
        ExportTaskJpaEntity entity = new ExportTaskJpaEntity();
        entity.setId(10L);
        entity.setTaskName("Export With Field Configs");
        entity.setProjectId(100L);
        entity.setStatus(ExportStatus.DRAFT);
        entity.setFileFormat(FileFormat.CSV);

        List<ExportFieldConfigJpaEntity> configs = new ArrayList<>();

        ExportFieldConfigJpaEntity fc1 = new ExportFieldConfigJpaEntity();
        fc1.setId(101L);
        fc1.setTaskId(10L);
        fc1.setFieldCode("age");
        fc1.setFieldLabel("Age");
        fc1.setSourceType("demographics");
        fc1.setCrfVersionId("v1.0");
        configs.add(fc1);

        ExportFieldConfigJpaEntity fc2 = new ExportFieldConfigJpaEntity();
        fc2.setId(102L);
        fc2.setTaskId(10L);
        fc2.setFieldCode("gender");
        fc2.setFieldLabel("Gender");
        fc2.setSourceType("demographics");
        fc2.setCrfVersionId("v1.0");
        configs.add(fc2);

        entity.setFieldConfigs(configs);

        springRepo.save(entity);
        em.flush();
        em.clear();

        // Read via Spring Data JPA to verify @OneToMany cascade
        Optional<ExportTaskJpaEntity> saved = springRepo.findById(10L);
        assertThat(saved).isPresent();
        assertThat(saved.get().getFieldConfigs()).hasSize(2);
        assertThat(saved.get().getFieldConfigs())
                .extracting("fieldCode")
                .containsExactlyInAnyOrder("age", "gender");
    }

    @Test
    void saveWithFilters() {
        ExportTaskJpaEntity entity = new ExportTaskJpaEntity();
        entity.setId(20L);
        entity.setTaskName("Export With Filters");
        entity.setProjectId(100L);
        entity.setStatus(ExportStatus.DRAFT);
        entity.setFileFormat(FileFormat.XLSX);

        List<ExportFilterJpaEntity> filters = new ArrayList<>();

        ExportFilterJpaEntity f1 = new ExportFilterJpaEntity();
        f1.setId(201L);
        f1.setTaskId(20L);
        f1.setFieldCode("age");
        f1.setOperator(ExportFilter.Operator.GT);
        f1.setFilterValue("18");
        f1.setLogicOperator(ExportFilter.LogicOperator.AND);
        filters.add(f1);

        ExportFilterJpaEntity f2 = new ExportFilterJpaEntity();
        f2.setId(202L);
        f2.setTaskId(20L);
        f2.setFieldCode("status");
        f2.setOperator(ExportFilter.Operator.EQ);
        f2.setFilterValue("ACTIVE");
        f2.setLogicOperator(null);
        filters.add(f2);

        entity.setFilters(filters);

        springRepo.save(entity);
        em.flush();
        em.clear();

        Optional<ExportTaskJpaEntity> saved = springRepo.findById(20L);
        assertThat(saved).isPresent();
        assertThat(saved.get().getFilters()).hasSize(2);
        assertThat(saved.get().getFilters())
                .extracting("fieldCode")
                .containsExactlyInAnyOrder("age", "status");
    }

    @Test
    void saveWithExecutionLogs() {
        ExportTaskJpaEntity entity = new ExportTaskJpaEntity();
        entity.setId(30L);
        entity.setTaskName("Export With Execution Logs");
        entity.setProjectId(100L);
        entity.setStatus(ExportStatus.COMPLETED);
        entity.setFileFormat(FileFormat.SAS);

        List<ExportExecutionLogJpaEntity> logs = new ArrayList<>();

        ExportExecutionLogJpaEntity log1 = new ExportExecutionLogJpaEntity();
        log1.setId(301L);
        log1.setTaskId(30L);
        log1.setStartTime(new Date(1700000000000L));
        log1.setEndTime(new Date(1700000100000L));
        log1.setStatus(ExportExecutionLog.ExecutionStatus.SUCCESS);
        log1.setRecordCount(250);
        log1.setFileName("export.sas7bdat");
        logs.add(log1);

        ExportExecutionLogJpaEntity log2 = new ExportExecutionLogJpaEntity();
        log2.setId(302L);
        log2.setTaskId(30L);
        log2.setStartTime(new Date(1700000200000L));
        log2.setEndTime(new Date(1700000300000L));
        log2.setStatus(ExportExecutionLog.ExecutionStatus.FAILED);
        log2.setRecordCount(0);
        log2.setFileName(null);
        logs.add(log2);

        entity.setExecutionLogs(logs);

        springRepo.save(entity);
        em.flush();
        em.clear();

        Optional<ExportTaskJpaEntity> saved = springRepo.findById(30L);
        assertThat(saved).isPresent();
        assertThat(saved.get().getExecutionLogs()).hasSize(2);

        // Verify first log details
        ExportExecutionLogJpaEntity firstLog = saved.get().getExecutionLogs().get(0);
        assertThat(firstLog.getStatus()).isEqualTo(ExportExecutionLog.ExecutionStatus.SUCCESS);
        assertThat(firstLog.getRecordCount()).isEqualTo(250);
        assertThat(firstLog.getFileName()).isEqualTo("export.sas7bdat");
    }

    @Test
    void getByIdNotFound() {
        assertThatThrownBy(() -> repo.getById(new ExportTaskId(9999L)))
                .isInstanceOf(AggregateNotFoundException.class);
    }

    @Test
    void findByProjectId() {
        ExportTaskJpaEntity task1 = new ExportTaskJpaEntity();
        task1.setId(40L);
        task1.setTaskName("Project 101 - A");
        task1.setProjectId(101L);
        task1.setStatus(ExportStatus.DRAFT);
        task1.setFileFormat(FileFormat.XLSX);

        ExportTaskJpaEntity task2 = new ExportTaskJpaEntity();
        task2.setId(41L);
        task2.setTaskName("Project 101 - B");
        task2.setProjectId(101L);
        task2.setStatus(ExportStatus.APPROVED);
        task2.setFileFormat(FileFormat.CSV);

        ExportTaskJpaEntity task3 = new ExportTaskJpaEntity();
        task3.setId(42L);
        task3.setTaskName("Project 102 - A");
        task3.setProjectId(102L);
        task3.setStatus(ExportStatus.DRAFT);
        task3.setFileFormat(FileFormat.XLSX);

        springRepo.save(task1);
        springRepo.save(task2);
        springRepo.save(task3);
        em.flush();
        em.clear();

        List<ExportTask> results = repo.findByProjectId("101");

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting("taskName")
                .containsExactlyInAnyOrder("Project 101 - A", "Project 101 - B");

        // Verify empty result for non-existent project
        List<ExportTask> empty = repo.findByProjectId("999");
        assertThat(empty).isEmpty();

        // Verify null/invalid projectId returns empty
        assertThat(repo.findByProjectId(null)).isEmpty();
        assertThat(repo.findByProjectId("not-a-number")).isEmpty();
    }

    @Test
    void saveUpdatesExisting() {
        // Arrange: persist a DRAFT task
        ExportTaskJpaEntity entity = new ExportTaskJpaEntity();
        entity.setId(50L);
        entity.setTaskName("Updatable Export");
        entity.setProjectId(100L);
        entity.setStatus(ExportStatus.DRAFT);
        entity.setFileFormat(FileFormat.XLSX);

        springRepo.save(entity);
        em.flush();
        em.clear();

        // Act: reconstruct with updated status APPROVED and audit trail
        ExportTask updated = ExportTask.reconstruct(
                new ExportTaskId(50L),
                "Updatable Export",
                new ProjectId(100L),
                null,
                null,
                ExportStatus.APPROVED,
                FileFormat.XLSX,
                "approver01",
                new Date(1700000000000L),
                "Approved for export",
                null,
                null,
                0,
                0,
                null,
                new ArrayList<ExportFieldConfig>(),
                new ArrayList<ExportFilter>(),
                new ArrayList<ExportExecutionLog>()
        );

        repo.save(updated);
        em.flush();
        em.clear();

        // Verify update took effect
        ExportTask found = repo.findById(new ExportTaskId(50L)).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getStatus()).isEqualTo(ExportStatus.APPROVED);
        assertThat(found.getAuditUserId()).isEqualTo("approver01");
        assertThat(found.getAuditMessage()).isEqualTo("Approved for export");
        assertThat(found.getAuditTime()).isNotNull();

        // Verify unchanged fields
        assertThat(found.getTaskName()).isEqualTo("Updatable Export");
        assertThat(found.getProjectId().getValue()).isEqualTo(100L);
        assertThat(found.getFileFormat()).isEqualTo(FileFormat.XLSX);
    }
}
