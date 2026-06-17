package com.clinicaltrial.ddd.statistics.domain.repository;

import com.clinicaltrial.ddd.common.model.AggregateNotFoundException;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;

import java.util.List;
import java.util.Optional;

/**
 * AnalysisProjectRepository — 分析项目仓储接口.
 * <p>
 * 提供AnalysisProject聚合的持久化操作。
 * 遵循仓储模式，每个聚合对应一个仓储。
 * 仓储负责聚合的生命周期管理，包括加载、保存和删除。
 * </p>
 */
public interface AnalysisProjectRepository {

    /**
     * 根据ID查找分析项目.
     *
     * @param id 分析项目ID
     * @return 包含AnalysisProject的Optional
     */
    Optional<AnalysisProject> findById(AnalysisProjectId id);

    /**
     * 根据ID获取分析项目，不存在时抛出异常.
     *
     * @param id 分析项目ID
     * @return AnalysisProject实例
     * @throws AggregateNotFoundException 如果未找到
     */
    default AnalysisProject getById(AnalysisProjectId id) {
        return findById(id)
                .orElseThrow(() -> new AggregateNotFoundException("AnalysisProject", id));
    }

    /**
     * 保存分析项目（新建或更新）.
     *
     * @param project AnalysisProject实例
     * @return 保存后的AnalysisProject实例
     */
    AnalysisProject save(AnalysisProject project);

    /**
     * 删除分析项目.
     *
     * @param id 分析项目ID
     */
    void delete(AnalysisProjectId id);

    /**
     * 查询所有分析项目.
     *
     * @return 分析项目列表
     */
    List<AnalysisProject> findAll();

    /**
     * 根据名称搜索分析项目.
     *
     * @param name 项目名称（模糊匹配）
     * @return 匹配的分析项目列表
     */
    List<AnalysisProject> findByNameContaining(String name);
}
