package com.clinicaltrial.ddd.statistics.application.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.statistics.application.command.ConfigureAnalysisCommand;
import com.clinicaltrial.ddd.statistics.application.command.ImportDataCommand;
import com.clinicaltrial.ddd.statistics.domain.model.aggregate.AnalysisProject;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisConfig;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.repository.AnalysisProjectRepository;

import java.util.Objects;

/**
 * AnalysisProjectApplicationService — 分析项目管理应用服务.
 * <p>
 * 编排分析项目管理的核心用例：创建项目、导入数据、配置分析。
 * 应用服务仅负责协调和编排，不包含业务逻辑——所有业务规则由领域层执行。
 * </p>
 *
 * <h3>职责</h3>
 * <ul>
 *   <li>接收应用指令并转换为领域操作</li>
 *   <li>校验输入参数的完整性</li>
 *   <li>加载和保存聚合</li>
 *   <li>发布领域事件</li>
 * </ul>
 */
@Service
public class AnalysisProjectApplicationService {

    private final AnalysisProjectRepository projectRepository;
    private final EventBus eventBus;

    /**
     * 构造AnalysisProjectApplicationService.
     *
     * @param projectRepository AnalysisProject仓储
     * @param eventBus          事件总线
     */
    public AnalysisProjectApplicationService(AnalysisProjectRepository projectRepository,
                                              EventBus eventBus) {
        this.projectRepository = projectRepository;
        this.eventBus = eventBus;
    }

    /**
     * 创建分析项目.
     *
     * @param name        项目名称
     * @param description 项目描述
     * @return 创建的AnalysisProject实例
     */
    public AnalysisProject createProject(String name, String description) {
        AnalysisProjectId id = generateProjectId();
        AnalysisProject project = AnalysisProject.create(id, name, description);

        AnalysisProject savedProject = projectRepository.save(project);
        eventBus.publishAll(savedProject);

        return savedProject;
    }

    /**
     * 导入数据到分析项目.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载AnalysisProject聚合</li>
     *   <li>调用 {@link AnalysisProject#importData(List)} 方法</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 导入数据指令
     * @return 更新后的AnalysisProject实例
     */
    public AnalysisProject importData(ImportDataCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load AnalysisProject aggregate
        AnalysisProject project = projectRepository.getById(command.getProjectId());

        // Step 2: Call domain method (infers variable types from data)
        project.importData(command.getRawData());

        // Step 3: Persist
        AnalysisProject savedProject = projectRepository.save(project);

        // Step 4: Publish domain events
        eventBus.publishAll(savedProject);

        return savedProject;
    }

    /**
     * 配置分析.
     * <p>
     * 用例流程：
     * <ol>
     *   <li>根据ID加载AnalysisProject聚合</li>
     *   <li>创建AnalysisConfig实体</li>
     *   <li>添加到项目中</li>
     *   <li>保存聚合</li>
     *   <li>发布领域事件</li>
     * </ol>
     * </p>
     *
     * @param command 配置分析指令
     * @return 更新后的AnalysisProject实例
     */
    public AnalysisProject configureAnalysis(ConfigureAnalysisCommand command) {
        Objects.requireNonNull(command, "command must not be null");

        // Step 1: Load AnalysisProject aggregate
        AnalysisProject project = projectRepository.getById(command.getProjectId());

        // Step 2: Create AnalysisConfig entity
        AnalysisConfig config = AnalysisConfig.create(
                generateAnalysisConfigId(),
                command.getName(),
                command.getAlgorithmType(),
                command.getDependentVariable(),
                command.getIndependentVariables(),
                command.getConfigJson()
        );

        // Step 3: Add to project (managed within aggregate boundary)
        project.getAnalysisConfigs().add(config);

        // Step 4: Persist
        AnalysisProject savedProject = projectRepository.save(project);

        // Step 5: Publish domain events
        eventBus.publishAll(savedProject);

        return savedProject;
    }

    /**
     * 生成分析项目ID（占位实现）.
     *
     * @return 新的AnalysisProjectId
     */
    private AnalysisProjectId generateProjectId() {
        return new AnalysisProjectId(System.currentTimeMillis());
    }

    /**
     * 生成分析配置ID（占位实现）.
     *
     * @return 新的AnalysisConfigId
     */
    private AnalysisConfigId generateAnalysisConfigId() {
        return new AnalysisConfigId(System.currentTimeMillis());
    }
}
