package com.clinicaltrial.ddd.statistics.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.statistics.application.command.ExecuteAnalysisCommand;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisConfig;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;
import com.clinicaltrial.ddd.statistics.domain.service.AnalysisExecutionService;

import java.util.Objects;

/**
 * AnalysisExecutionApplicationService — 分析执行应用服务.
 * <p>
 * 编排分析执行的核心用例：执行预设的分析配置并生成结果。
 * 应用服务仅负责协调和编排，不包含业务逻辑——所有业务规则由领域层执行。
 * </p>
 */
@Service
public class AnalysisExecutionApplicationService {

    private final AnalysisProjectRepository projectRepository;
    private final AnalysisExecutionService analysisExecutionService;
    private final EventBus eventBus;

    /**
     * 构造AnalysisExecutionApplicationService.
     *
     * @param projectRepository       AnalysisProject仓储
     * @param analysisExecutionService 分析执行领域服务
     * @param eventBus                 事件总线
     */
    public AnalysisExecutionApplicationService(AnalysisProjectRepository projectRepository,
                                                AnalysisExecutionService analysisExecutionService,
                                                EventBus eventBus) {
        this.projectRepository = projectRepository;
        this.analysisExecutionService = analysisExecutionService;
        this.eventBus = eventBus;
    }

    /**
     * 执行分析.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载AnalysisProject聚合</li>
     *   <li>查找指定的分析配置</li>
     *   <li>调用领域服务执行分析算法</li>
     *   <li>将结果添加到项目中</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 执行分析指令
     * @return 更新后的AnalysisProject实例
     * @throws com.clinicaltrial.ddd.common.model.BusinessRuleViolationException
     *               如果变量不存在或状态不允许
     */
    public AnalysisProject executeAnalysis(ExecuteAnalysisCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load AnalysisProject aggregate
        AnalysisProject project = projectRepository.getById(command.getProjectId());

        // Step 2: Find the analysis config
        AnalysisConfig config = project.getAnalysisConfigs().stream()
                .filter(c -> c.getId().equals(command.getConfigId()))
                .findFirst()
                .orElseThrow(() -> new BusinessRuleViolationException(
                        "ANALYSIS_CONFIG_NOT_FOUND",
                        "AnalysisConfig " + command.getConfigId() + " not found in project"));

        // Step 3: Execute analysis via domain service
        AnalysisResult result = analysisExecutionService.execute(config, null);

        // Step 4: Add result to project
        project.addResult(result);

        // Step 5: Persist
        AnalysisProject savedProject = projectRepository.save(project);

        // Step 6: Publish domain events
        eventBus.publishAll(savedProject);

        return savedProject;
    }
}
