package com.clinicaltrial.ddd.dataexport.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

import java.util.List;
import java.util.Optional;

/**
 * ExportTaskRepository — 导出任务仓储接口.
 * <p>
 * 提供ExportTask聚合的持久化操作。
 * 遵循仓储模式，每个聚合对应一个仓储。
 * 仓储负责聚合的生命周期管理，包括加载、保存和删除。
 * </p>
 */
public interface ExportTaskRepository {

    /**
     * 根据ID查找导出任务.
     *
     * @param id 导出任务ID
     * @return 包含ExportTask的Optional
     */
    Optional<ExportTask> findById(ExportTaskId id);

    /**
     * 根据ID获取导出任务，不存在时抛出异常.
     *
     * @param id 导出任务ID
     * @return ExportTask实例
     * @throws AggregateNotFoundException 如果未找到
     */
    default ExportTask getById(ExportTaskId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("ExportTask", id));
    }

    /**
     * 保存导出任务（新建或更新）.
     *
     * @param exportTask ExportTask实例
     * @return 保存后的ExportTask实例
     */
    ExportTask save(ExportTask exportTask);

    /**
     * 根据项目ID查询所有导出任务.
     *
     * @param projectId 项目ID（字符串形式）
     * @return 导出任务列表
     */
    List<ExportTask> findByProjectId(String projectId);

    /**
     * 查询所有导出任务.
     *
     * @return 导出任务列表
     */
    List<ExportTask> findAll();

    /**
     * 检查指定项目下是否存在同名导出任务.
     *
     * @param taskName  任务名称
     * @param projectId 项目ID（字符串形式）
     * @return true 如果已存在同名任务
     */
    boolean existsByTaskNameAndProjectId(String taskName, String projectId);

    /**
     * 检查指定项目下是否存在同名导出任务（排除指定ID）.
     *
     * @param taskName  任务名称
     * @param projectId 项目ID（字符串形式）
     * @param excludeId 排除的导出任务ID
     * @return true 如果已存在同名任务（非指定ID）
     */
    boolean existsByTaskNameAndProjectIdExcludingId(String taskName, String projectId,
                                                     ExportTaskId excludeId);
}
