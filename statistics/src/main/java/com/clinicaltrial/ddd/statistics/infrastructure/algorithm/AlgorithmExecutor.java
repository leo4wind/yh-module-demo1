package com.clinicaltrial.ddd.statistics.infrastructure.algorithm;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;

import java.util.List;
import java.util.Map;

/**
 * AlgorithmExecutor — 统计算法执行器策略接口.
 * <p>
 * 定义统计分析算法的执行策略，支持多种统计方法的实现。
 * 每个实现类负责执行一种特定的统计算法并返回结果。
 * </p>
 *
 * <p>
 * 实现类：
 * <ul>
 *   <li>{@link ChiSquareExecutor} — 卡方检验</li>
 *   <li>{@link TTestExecutor} — t检验</li>
 *   <li>{@link AnovaExecutor} — 方差分析</li>
 *   <li>{@link RegressionExecutor} — 回归分析</li>
 * </ul>
 * </p>
 */
public interface AlgorithmExecutor {

    /**
     * 执行统计算法.
     *
     * @param dependentVariable    因变量名称
     * @param independentVariables 自变量名称列表
     * @param data                 分析数据（每行为变量名-值的映射）
     * @param params               额外参数（JSON格式）
     * @return 算法执行结果（JSON格式字符串）
     */
    String execute(String dependentVariable,
                   List<String> independentVariables,
                   List<Map<String, Object>> data,
                   String params);

    /**
     * 获取此执行器支持的算法类型.
     *
     * @return AlgorithmType
     */
    AlgorithmType supportedAlgorithm();
}
