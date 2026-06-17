package com.clinicaltrial.ddd.dataexport.infrastructure.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportExecutionLog;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFieldConfig;
import com.clinicaltrial.ddd.dataexport.domain.model.entity.ExportFilter;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportExecutionLogId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFieldConfigId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportFilterId;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.repository.ExportTaskRepository;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportExecutionLogJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportFieldConfigJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportFilterJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportTaskJpaEntity;
import com.clinicaltrial.ddd.dataexport.infrastructure.persistence.ExportTaskSpringDataRepo;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.ProjectId;
import com.clinicaltrial.ddd.trial.domain.model.valueobject.StageId;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository implementation for ExportTask aggregate.
 * Maps between domain ExportTask aggregate and JPA entities.
 */
@Repository
@Transactional
public class ExportTaskRepositoryImpl implements ExportTaskRepository {

    private final ExportTaskSpringDataRepo springDataRepo;

    public ExportTaskRepositoryImpl(ExportTaskSpringDataRepo springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExportTask> findById(ExportTaskId id) {
        return springDataRepo.findById(id.getValue())
                .map(this::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public ExportTask getById(ExportTaskId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("ExportTask", id));
    }

    @Override
    @Transactional
    public ExportTask save(ExportTask exportTask) {
        ExportTaskJpaEntity entity = toJpa(exportTask);
        ExportTaskJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExportTask> findByProjectId(String projectId) {
        Long projectIdLong = parseProjectId(projectId);
        if (projectIdLong == null) {
            return Collections.emptyList();
        }
        return springDataRepo.findByProjectId(projectIdLong).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExportTask> findAll() {
        return springDataRepo.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTaskNameAndProjectId(String taskName, String projectId) {
        Long projectIdLong = parseProjectId(projectId);
        if (projectIdLong == null) {
            return false;
        }
        return springDataRepo.existsByTaskNameAndProjectId(taskName, projectIdLong);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByTaskNameAndProjectIdExcludingId(String taskName, String projectId,
                                                           ExportTaskId excludeId) {
        Long projectIdLong = parseProjectId(projectId);
        if (projectIdLong == null) {
            return false;
        }
        return springDataRepo.existsByTaskNameAndProjectIdAndIdNot(
                taskName, projectIdLong, excludeId.getValue());
    }

    // ========== Domain -> JPA mapping ==========

    private ExportTaskJpaEntity toJpa(ExportTask domain) {
        ExportTaskJpaEntity entity = new ExportTaskJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setTaskName(domain.getTaskName());
        entity.setProjectId(domain.getProjectId() != null ? domain.getProjectId().getValue() : null);
        entity.setStageId(domain.getStageId() != null ? domain.getStageId().getValue() : null);
        entity.setCrfVersionId(domain.getCrfVersionId() != null ? domain.getCrfVersionId().getValue() : null);
        entity.setStatus(domain.getStatus());
        entity.setFileFormat(domain.getFileFormat());
        entity.setAuditUserId(domain.getAuditUserId());
        entity.setAuditTime(domain.getAuditTime());
        entity.setAuditMessage(domain.getAuditMessage());
        entity.setFileUrl(domain.getFileUrl());
        entity.setFileName(domain.getFileName());
        entity.setDownloadCount(domain.getDownloadCount());
        entity.setFailCount(domain.getFailCount());
        entity.setFailMessage(domain.getFailMessage());

        if (domain.getFieldConfigs() != null) {
            entity.setFieldConfigs(domain.getFieldConfigs().stream()
                    .map(fc -> toJpaFieldConfig(fc, entity))
                    .collect(Collectors.toList()));
        }

        if (domain.getFilters() != null) {
            entity.setFilters(domain.getFilters().stream()
                    .map(f -> toJpaFilter(f, entity))
                    .collect(Collectors.toList()));
        }

        if (domain.getExecutionLogs() != null) {
            entity.setExecutionLogs(domain.getExecutionLogs().stream()
                    .map(log -> toJpaExecutionLog(log, entity))
                    .collect(Collectors.toList()));
        }

        return entity;
    }

    private ExportFieldConfigJpaEntity toJpaFieldConfig(ExportFieldConfig domain,
                                                         ExportTaskJpaEntity parent) {
        ExportFieldConfigJpaEntity entity = new ExportFieldConfigJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setTaskId(parent.getId());
        entity.setFieldCode(domain.getFieldCode());
        entity.setFieldLabel(domain.getFieldLabel());
        entity.setSourceType(domain.getSourceType());
        entity.setCrfVersionId(domain.getCrfVersionId());
        return entity;
    }

    private ExportFilterJpaEntity toJpaFilter(ExportFilter domain,
                                               ExportTaskJpaEntity parent) {
        ExportFilterJpaEntity entity = new ExportFilterJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setTaskId(parent.getId());
        entity.setFieldCode(domain.getFieldCode());
        entity.setOperator(domain.getOperator());
        entity.setFilterValue(domain.getFilterValue());
        entity.setLogicOperator(domain.getLogicOperator());
        return entity;
    }

    private ExportExecutionLogJpaEntity toJpaExecutionLog(ExportExecutionLog domain,
                                                           ExportTaskJpaEntity parent) {
        ExportExecutionLogJpaEntity entity = new ExportExecutionLogJpaEntity();
        if (domain.getId() != null) {
            entity.setId(domain.getId().getValue());
        }
        entity.setTaskId(parent.getId());
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        entity.setStatus(domain.getStatus());
        entity.setRecordCount(domain.getRecordCount());
        entity.setFileName(domain.getFileName());
        return entity;
    }

    // ========== JPA -> Domain mapping ==========

    private ExportTask toDomain(ExportTaskJpaEntity entity) {
        List<ExportFieldConfig> fieldConfigs = entity.getFieldConfigs() != null
                ? entity.getFieldConfigs().stream().map(this::toDomainFieldConfig)
                    .collect(Collectors.toList())
                : Collections.<ExportFieldConfig>emptyList();

        List<ExportFilter> filters = entity.getFilters() != null
                ? entity.getFilters().stream().map(this::toDomainFilter)
                    .collect(Collectors.toList())
                : Collections.<ExportFilter>emptyList();

        List<ExportExecutionLog> executionLogs = entity.getExecutionLogs() != null
                ? entity.getExecutionLogs().stream().map(this::toDomainExecutionLog)
                    .collect(Collectors.toList())
                : Collections.<ExportExecutionLog>emptyList();

        ProjectId projectId = entity.getProjectId() != null
                ? new ProjectId(entity.getProjectId()) : null;
        StageId stageId = entity.getStageId() != null
                ? new StageId(entity.getStageId()) : null;
        CrfVersionId crfVersionId = entity.getCrfVersionId() != null
                ? new CrfVersionId(entity.getCrfVersionId()) : null;

        return ExportTask.reconstruct(
                new ExportTaskId(entity.getId()),
                entity.getTaskName(),
                projectId,
                stageId,
                crfVersionId,
                entity.getStatus(),
                entity.getFileFormat(),
                entity.getAuditUserId(),
                entity.getAuditTime(),
                entity.getAuditMessage(),
                entity.getFileUrl(),
                entity.getFileName(),
                entity.getDownloadCount(),
                entity.getFailCount(),
                entity.getFailMessage(),
                fieldConfigs,
                filters,
                executionLogs
        );
    }

    private ExportFieldConfig toDomainFieldConfig(ExportFieldConfigJpaEntity entity) {
        return ExportFieldConfig.create(
                new ExportFieldConfigId(entity.getId()),
                null,
                entity.getFieldCode(),
                entity.getFieldLabel(),
                entity.getSourceType(),
                entity.getCrfVersionId()
        );
    }

    private ExportFilter toDomainFilter(ExportFilterJpaEntity entity) {
        return ExportFilter.create(
                new ExportFilterId(entity.getId()),
                null,
                entity.getFieldCode(),
                entity.getOperator(),
                entity.getFilterValue(),
                entity.getLogicOperator()
        );
    }

    private ExportExecutionLog toDomainExecutionLog(ExportExecutionLogJpaEntity entity) {
        ExportExecutionLog log = ExportExecutionLog.start(
                new ExportExecutionLogId(entity.getId()),
                null
        );
        if (entity.getStatus() == ExportExecutionLog.ExecutionStatus.SUCCESS) {
            log.markCompleted(entity.getRecordCount(), entity.getFileName());
        } else if (entity.getStatus() == ExportExecutionLog.ExecutionStatus.FAILED) {
            log.markFailed();
        }
        return log;
    }

    // ========== Helper ==========

    private Long parseProjectId(String projectId) {
        if (projectId == null || projectId.trim().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(projectId.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
