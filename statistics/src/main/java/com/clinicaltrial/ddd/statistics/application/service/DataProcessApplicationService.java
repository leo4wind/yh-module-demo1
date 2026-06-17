package com.clinicaltrial.ddd.statistics.application.service;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.statistics.application.command.ProcessDataCommand;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.entity.DataProcessStep;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;

import java.util.List;
import java.util.Objects;

/**
 * DataProcessApplicationService — 数据处理应用服务.
 * <p>
 * 编排数据处理的核心用例：添加处理步骤、执行数据处理。
 * 应用服务仅负责协调和编排，不包含业务逻辑——所有业务规则由领域层执行。
 * </p>
 */
public class DataProcessApplicationService {

    private final AnalysisProjectRepository projectRepository;
    private final EventBus eventBus;

    /**
     * 构造DataProcessApplicationService.
     *
     * @param projectRepository AnalysisProject仓储
     * @param eventBus          事件总线
     */
    public DataProcessApplicationService(AnalysisProjectRepository projectRepository,
                                          EventBus eventBus) {
        this.projectRepository = projectRepository;
        this.eventBus = eventBus;
    }

    /**
     * 执行数据处理.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载AnalysisProject聚合</li>
     *   <li>调用 {@link AnalysisProject#executeProcessing()} 方法</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 处理数据指令
     * @return 更新后的AnalysisProject实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果没有定义处理步骤
     */
    public AnalysisProject processData(ProcessDataCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load AnalysisProject aggregate
        AnalysisProject project = projectRepository.getById(command.getProjectId());

        // Step 2: Call domain method
        project.executeProcessing();

        // Step 3: Persist
        AnalysisProject savedProject = projectRepository.save(project);

        // Step 4: Publish domain events
        eventBus.publishAll(savedProject);

        return savedProject;
    }

    /**
     * 添加数据处理步骤到分析项目.
     *
     * @param projectId 分析项目ID
     * @param step      数据处理步骤
     * @return 更新后的AnalysisProject实例
     */
    public AnalysisProject addProcessStep(AnalysisProjectId projectId, DataProcessStep step) {
        Objects.requireNonNull(projectId, "projectId must not be null");
        Objects.requireNonNull(step, "step must not be null");

        AnalysisProject project = projectRepository.getById(projectId);
        project.addProcessStep(step);

        AnalysisProject savedProject = projectRepository.save(project);
        eventBus.publishAll(savedProject);

        return savedProject;
    }
}
