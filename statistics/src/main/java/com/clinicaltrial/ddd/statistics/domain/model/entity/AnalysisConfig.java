package com.clinicaltrial.ddd.statistics.domain.model.entity;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.common.model.Entity;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisConfigId;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.ResultStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * AnalysisConfig — 分析配置实体.
 * <p>
 * 定义一项统计分析的具体配置，包括算法类型、因变量、自变量列表、
 * 额外参数和执行状态。
 * 一个分析项目可以包含多个分析配置，每个配置执行一种分析。
 * </p>
 */
public class AnalysisConfig extends Entity<AnalysisConfigId> {

    private AnalysisConfigId id;
    private String name;
    private AlgorithmType algorithmType;
    private String dependentVariable;
    private List<String> independentVariables;
    private String configJson;
    private ResultStatus status;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private AnalysisConfig() {
        this.independentVariables = new ArrayList<>();
        this.status = ResultStatus.PENDING;
    }

    /**
     * 创建分析配置.
     *
     * @param id                  配置ID
     * @param name                配置名称
     * @param algorithmType       算法类型
     * @param dependentVariable   因变量名称
     * @param independentVariables 自变量名称列表
     * @param configJson          额外配置参数（JSON格式，可为null）
     * @return AnalysisConfig实例
     */
    public static AnalysisConfig create(AnalysisConfigId id,
                                         String name,
                                         AlgorithmType algorithmType,
                                         String dependentVariable,
                                         List<String> independentVariables,
                                         String configJson) {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(algorithmType, "algorithmType must not be null");
        Objects.requireNonNull(dependentVariable, "dependentVariable must not be null");

        AnalysisConfig config = new AnalysisConfig();
        config.id = id;
        config.name = name;
        config.algorithmType = algorithmType;
        config.dependentVariable = dependentVariable;
        config.independentVariables = independentVariables != null
                ? new ArrayList<>(independentVariables) : new ArrayList<String>();
        config.configJson = configJson;
        return config;
    }

    /**
     * 标记为执行中.
     *
     * @throws BusinessRuleViolationException 如果当前状态不是PENDING或FAILED
     */
    public void markRunning() {
        if (status != ResultStatus.PENDING && status != ResultStatus.FAILED) {
            throw new BusinessRuleViolationException(
                    "ANALYSIS_INVALID_TRANSITION",
                    "Cannot start analysis " + id + ": current status is " + status
                            + ", expected PENDING or FAILED");
        }
        this.status = ResultStatus.RUNNING;
    }

    /**
     * 标记为已完成.
     *
     * @throws BusinessRuleViolationException 如果当前状态不是RUNNING
     */
    public void markCompleted() {
        if (status != ResultStatus.RUNNING) {
            throw new BusinessRuleViolationException(
                    "ANALYSIS_INVALID_TRANSITION",
                    "Cannot complete analysis " + id + ": current status is " + status
                            + ", expected RUNNING");
        }
        this.status = ResultStatus.COMPLETED;
    }

    /**
     * 标记为失败.
     *
     * @throws BusinessRuleViolationException 如果当前状态不是RUNNING
     */
    public void markFailed() {
        if (status != ResultStatus.RUNNING) {
            throw new BusinessRuleViolationException(
                    "ANALYSIS_INVALID_TRANSITION",
                    "Cannot fail analysis " + id + ": current status is " + status
                            + ", expected RUNNING");
        }
        this.status = ResultStatus.FAILED;
    }

    @Override
    public AnalysisConfigId getId() {
        return id;
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
     * 获取自变量名称列表（不可修改）.
     *
     * @return 自变量列表
     */
    public List<String> getIndependentVariables() {
        return Collections.unmodifiableList(independentVariables);
    }

    /**
     * 获取额外配置参数（JSON格式）.
     *
     * @return JSON字符串，可能为null
     */
    public String getConfigJson() {
        return configJson;
    }

    /**
     * 获取执行状态.
     *
     * @return ResultStatus
     */
    public ResultStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "AnalysisConfig{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", algorithmType=" + algorithmType
                + ", status=" + status
                + '}';
    }
}
