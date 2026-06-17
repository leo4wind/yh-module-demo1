package com.clinicaltrial.ddd.statistics.application.command;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;

import java.util.Objects;

/**
 * ExecuteAnalysisCommand — 执行分析指令.
 * <p>
 * 封装执行一项预设分析配置所需的输入参数。
 * 触发指定的分析配置运行并生成分析结果。
 * </p>
 */
public class ExecuteAnalysisCommand {

    private final AnalysisProjectId projectId;
    private final AnalysisConfigId configId;

    /**
     * 构造ExecuteAnalysisCommand.
     *
     * @param projectId 分析项目ID
     * @param configId  分析配置ID
     */
    public ExecuteAnalysisCommand(AnalysisProjectId projectId, AnalysisConfigId configId) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.configId = Objects.requireNonNull(configId, "configId must not be null");
    }

    /**
     * 获取分析项目ID.
     *
     * @return AnalysisProjectId
     */
    public AnalysisProjectId getProjectId() {
        return projectId;
    }

    /**
     * 获取分析配置ID.
     *
     * @return AnalysisConfigId
     */
    public AnalysisConfigId getConfigId() {
        return configId;
    }
}
