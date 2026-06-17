package com.clinicaltrial.ddd.statistics.application.command;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisProjectId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;

import java.util.List;
import java.util.Objects;

/**
 * ConfigureAnalysisCommand — 配置分析指令.
 * <p>
 * 封装在分析项目中配置一项统计分析所需的输入参数。
 * 包含算法类型、因变量、自变量和额外参数。
 * </p>
 */
public class ConfigureAnalysisCommand {

    private final AnalysisProjectId projectId;
    private final String name;
    private final AlgorithmType algorithmType;
    private final String dependentVariable;
    private final List<String> independentVariables;
    private final String configJson;

    /**
     * 构造ConfigureAnalysisCommand.
     *
     * @param projectId           分析项目ID
     * @param name                配置名称
     * @param algorithmType       算法类型
     * @param dependentVariable   因变量名称
     * @param independentVariables 自变量名称列表
     * @param configJson          额外配置参数（JSON格式，可为null）
     */
    public ConfigureAnalysisCommand(AnalysisProjectId projectId,
                                     String name,
                                     AlgorithmType algorithmType,
                                     String dependentVariable,
                                     List<String> independentVariables,
                                     String configJson) {
        this.projectId = Objects.requireNonNull(projectId, "projectId must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.algorithmType = Objects.requireNonNull(algorithmType, "algorithmType must not be null");
        this.dependentVariable = Objects.requireNonNull(dependentVariable, "dependentVariable must not be null");
        this.independentVariables = independentVariables;
        this.configJson = configJson;
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
     * 获取配置名称.
     *
     * @return 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取算法类型.
     *
     * @return AlgorithmType
     */
    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    /**
     * 获取因变量名称.
     *
     * @return 因变量
     */
    public String getDependentVariable() {
        return dependentVariable;
    }

    /**
     * 获取自变量名称列表.
     *
     * @return 自变量列表
     */
    public List<String> getIndependentVariables() {
        return independentVariables;
    }

    /**
     * 获取额外配置参数.
     *
     * @return JSON字符串，可能为null
     */
    public String getConfigJson() {
        return configJson;
    }
}
