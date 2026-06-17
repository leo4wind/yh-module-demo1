package com.clinicaltrial.ddd.statistics.application.service;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.statistics.application.command.SaveResultCommand;
import com.clinicaltrial.ddd.statistics.domain.event.ResultFavoritedEvent;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;

import java.util.Objects;

/**
 * ResultManagementApplicationService — 分析结果管理应用服务.
 * <p>
 * 编排分析结果管理的核心用例：更新结果、切换收藏。
 * 应用服务仅负责协调和编排，不包含业务逻辑——所有业务规则由领域层执行。
 * </p>
 */
public class ResultManagementApplicationService {

    private final AnalysisProjectRepository projectRepository;
    private final EventBus eventBus;

    /**
     * 构造ResultManagementApplicationService.
     *
     * @param projectRepository AnalysisProject仓储
     * @param eventBus          事件总线
     */
    public ResultManagementApplicationService(AnalysisProjectRepository projectRepository,
                                               EventBus eventBus) {
        this.projectRepository = projectRepository;
        this.eventBus = eventBus;
    }

    /**
     * 收藏或取消收藏分析结果.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载AnalysisProject聚合</li>
     *   <li>查找指定的分析结果</li>
     *   <li>切换收藏状态</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 保存结果指令
     * @return 更新后的AnalysisProject实例
     */
    public AnalysisProject toggleFavorite(SaveResultCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load AnalysisProject aggregate
        AnalysisProject project = projectRepository.getById(command.getProjectId());

        // Step 2: Find the analysis result
        AnalysisResult result = project.getResults().stream()
                .filter(r -> r.getId().equals(command.getResultId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "AnalysisResult " + command.getResultId() + " not found in project"));

        // Step 3: Toggle favorite status
        if (Boolean.TRUE.equals(command.getFavorite())) {
            result.markAsFavorite();
        } else if (Boolean.FALSE.equals(command.getFavorite())) {
            result.unmarkAsFavorite();
        }

        // Step 4: Persist
        AnalysisProject savedProject = projectRepository.save(project);

        // Step 5: Publish domain events
        eventBus.publishAll(savedProject);

        // Also publish the result-specific favorited event
        if (command.getFavorite() != null) {
            eventBus.publish(new ResultFavoritedEvent(
                    command.getProjectId(),
                    command.getResultId(),
                    command.getFavorite()));
        }

        return savedProject;
    }

    /**
     * 更新分析结果数据.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载AnalysisProject聚合</li>
     *   <li>查找指定的分析结果</li>
     *   <li>创建新的结果实体替换旧结果</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 保存结果指令
     * @return 更新后的AnalysisProject实例
     */
    public AnalysisProject updateResult(SaveResultCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        AnalysisProject project = projectRepository.getById(command.getProjectId());

        AnalysisResult existingResult = project.getResults().stream()
                .filter(r -> r.getId().equals(command.getResultId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "AnalysisResult " + command.getResultId() + " not found in project"));

        // Remove and recreate with updated data
        project.getResults().remove(existingResult);

        AnalysisResult updatedResult = AnalysisResult.create(
                command.getResultId(),
                existingResult.getName(),
                existingResult.getMethod(),
                command.getData() != null ? command.getData() : existingResult.getData(),
                command.getResultSummary() != null ? command.getResultSummary() : existingResult.getResultSummary(),
                existingResult.getParams()
        );

        project.addResult(updatedResult);

        AnalysisProject savedProject = projectRepository.save(project);
        eventBus.publishAll(savedProject);

        return savedProject;
    }
}
