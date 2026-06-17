package com.clinicaltrial.ddd.statistics.infrastructure.algorithm;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AlgorithmType;

import java.util.List;
import java.util.Map;

/**
 * AnovaExecutor — 方差分析算法执行器桩实现.
 * <p>
 * 执行单因素方差分析（One-Way ANOVA），
 * 用于比较三个及以上组别的均值是否存在显著差异。
 * 当前为桩（Stub）实现，等待集成后完成。
 * </p>
 */
public class AnovaExecutor implements AlgorithmExecutor {

    @Override
    public String execute(String dependentVariable,
                           List<String> independentVariables,
                           List<Map<String, Object>> data,
                           String params) {
        // TODO: 使用Apache Commons Math的OneWayAnova实现
        // 1. 根据分组变量分离数据为多组
        // 2. 计算F统计量
        // 3. 构建结果JSON（包含F值、组间平方和、组内平方和、p值等）
        throw new UnsupportedOperationException("AnovaExecutor not yet implemented");
    }

    @Override
    public AlgorithmType supportedAlgorithm() {
        return AlgorithmType.ANOVA;
    }
}
