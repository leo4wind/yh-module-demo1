package com.clinicaltrial.ddd.dataexport.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;
import com.clinicaltrial.ddd.dataexport.domain.repository.ExportTaskRepository;

import java.util.Objects;

/**
 * ExportDuplicateDetectionServiceImpl — 导出重复检测领域服务实现.
 */
@Service
public class ExportDuplicateDetectionServiceImpl implements ExportDuplicateDetectionService {

    private final ExportTaskRepository exportTaskRepository;

    public ExportDuplicateDetectionServiceImpl(ExportTaskRepository exportTaskRepository) {
        this.exportTaskRepository = exportTaskRepository;
    }

    @Override
    public void validateTaskNameUnique(String taskName, String projectId)
            throws BusinessRuleViolationException {
        Objects.requireNonNull(taskName, "taskName must not be null");
        Objects.requireNonNull(projectId, "projectId must not be null");

        if (exportTaskRepository.existsByTaskNameAndProjectId(taskName, projectId)) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_EXPORT_TASK",
                    "项目下已存在同名导出任务: " + taskName);
        }
    }

    @Override
    public void validateTaskNameUniqueExcludingSelf(ExportTaskId taskId, String taskName, String projectId)
            throws BusinessRuleViolationException {
        Objects.requireNonNull(taskId, "taskId must not be null");
        Objects.requireNonNull(taskName, "taskName must not be null");
        Objects.requireNonNull(projectId, "projectId must not be null");

        if (exportTaskRepository.existsByTaskNameAndProjectIdExcludingId(taskName, projectId, taskId)) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_EXPORT_TASK",
                    "项目下已存在同名导出任务（非自身）: " + taskName);
        }
    }
}
