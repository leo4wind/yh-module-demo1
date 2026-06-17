package com.clinicaltrial.ddd.statistics.domain.service;

import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisConfig;
import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;

import java.util.List;
import java.util.Map;

/**
 * AnalysisExecutionService — 分析执行领域服务.
 * <p>
 * 负责执行统计分析的核心逻辑，通过策略模式委托给具体的算法执行器实现。
 * 协调数据准备、算法执行和结果生成的完整流程。
 * </p>
 *
 * <p>
 * 职责：
 * <ul>
 *   <li>根据分析配置准备输入数据</li>
 *   <li>选择合适的算法执行器</li>
 *   <li>执行统计算法并生成结果</li>
 *   <li>保存结果到分析项目中</li>
 * </ul>
 * </p>
 */
public interface AnalysisExecutionService {

    /**
     * 执行统计分析.
     *
     * @param config  分析配置
     * @param data    分析数据（变量名到值的映射列表）
     * @return AnalysisResult 包含分析结果数据
     */
    AnalysisResult execute(AnalysisConfig config, List<Map<String, Object>> data);
}
