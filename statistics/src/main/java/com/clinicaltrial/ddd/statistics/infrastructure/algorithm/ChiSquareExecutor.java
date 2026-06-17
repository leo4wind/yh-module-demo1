package com.clinicaltrial.ddd.statistics.infrastructure.algorithm;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;

import java.util.List;
import java.util.Map;

/**
 * ChiSquareExecutor — 卡方检验算法执行器桩实现.
 * <p>
 * 执行卡方检验（Chi-Square Test），用于检验两个分类变量之间的关联性。
 * 当前为桩（Stub）实现，等待集成Apache Commons Math或其他统计库后完成。
 * </p>
 */
public class ChiSquareExecutor implements AlgorithmExecutor {

    @Override
    public String execute(String dependentVariable,
                           List<String> independentVariables,
                           List<Map<String, Object>> data,
                           String params) {
        // TODO: 使用Apache Commons Math的ChiSquareTest实现
        // 1. 构建列联表（contingency table）
        // 2. 计算卡方统计量和p值
        // 3. 构建结果JSON（包含卡方值、自由度、p值、期望频数等）
        throw new UnsupportedOperationException("ChiSquareExecutor not yet implemented");
    }

    @Override
    public AlgorithmType supportedAlgorithm() {
        return AlgorithmType.CHI_SQUARE;
    }
}
