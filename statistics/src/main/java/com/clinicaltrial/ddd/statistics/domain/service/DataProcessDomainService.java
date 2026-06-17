package com.clinicaltrial.ddd.statistics.domain.service;

import com.clinicaltrial.ddd.statistics.domain.model.entity.DataProcessStep;

import java.util.List;
import java.util.Map;

/**
 * DataProcessDomainService — 数据处理领域服务.
 * <p>
 * 负责执行数据分析前的数据预处理操作。
 * 根据配置的步骤类型执行相应的数据转换逻辑。
 * </p>
 *
 * <p>
 * 支持的处理类型：
 * <ul>
 *   <li>CATEGORIZE — 将连续变量按阈值分类</li>
 *   <li>MERGE — 合并多个变量的值</li>
 *   <li>SPLIT — 拆分为多个变量</li>
 *   <li>FILL_MISSING — 缺失值填充（均值、中位数、众数或指定值）</li>
 *   <li>NORMALIZE — 归一化（Min-Max或Z-score）</li>
 * </ul>
 * </p>
 */
public interface DataProcessDomainService {

    /**
     * 执行单个数据处理步骤.
     *
     * @param data       原始数据
     * @param step       处理步骤定义
     * @return 处理后的数据
     */
    List<Map<String, Object>> processStep(List<Map<String, Object>> data, DataProcessStep step);

    /**
     * 执行多个数据处理步骤（按sortOrder顺序）.
     *
     * @param data   原始数据
     * @param steps  处理步骤列表（将按sortOrder排序后依次执行）
     * @return 处理后的数据
     */
    List<Map<String, Object>> processAll(List<Map<String, Object>> data, List<DataProcessStep> steps);
}
