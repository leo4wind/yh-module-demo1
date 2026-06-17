package com.clinicaltrial.ddd.statistics.infrastructure.algorithm;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;

import java.util.List;
import java.util.Map;

/**
 * TTestExecutor — t检验算法执行器桩实现.
 * <p>
 * 执行独立样本t检验（Independent Samples t-Test），
 * 用于比较两组数据的均值是否存在显著差异。
 * 当前为桩（Stub）实现，等待集成后完成。
 * </p>
 */
public class TTestExecutor implements AlgorithmExecutor {

    @Override
    public String execute(String dependentVariable,
                           List<String> independentVariables,
                           List<Map<String, Object>> data,
                           String params) {
        // TODO: 使用Apache Commons Math的TTest实现
        // 1. 根据分组变量分离数据为两组
        // 2. 计算t统计量
        // 3. 构建结果JSON（包含t值、自由度、p值、均值差、置信区间等）
        throw new UnsupportedOperationException("TTestExecutor not yet implemented");
    }

    @Override
    public AlgorithmType supportedAlgorithm() {
        return AlgorithmType.T_TEST;
    }
}
