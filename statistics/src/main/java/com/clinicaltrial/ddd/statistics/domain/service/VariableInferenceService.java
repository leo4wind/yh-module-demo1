package com.clinicaltrial.ddd.statistics.domain.service;

import com.clinicaltrial.ddd.statistics.domain.model.valueobject.VariableType;

import java.util.List;
import java.util.Map;

/**
 * VariableInferenceService — 变量类型推断领域服务.
 * <p>
 * 根据导入数据的实际内容推断每个变量的数据类型。
 * 支持多种推断策略：基于样本值、基于完整列数据统计、基于字段名模式匹配。
 * </p>
 *
 * <p>
 * 推断规则：
 * <ul>
 *   <li>如果列中所有值均为数字 → NUMERIC</li>
 *   <li>如果列中唯一值数量少于总行数的5%（且行数大于20）→ CATEGORICAL</li>
 *   <li>如果值符合日期格式 → DATE</li>
 *   <li>如果列中唯一值有明确的顺序关系 → ORDINAL</li>
 *   <li>其他情况 → TEXT</li>
 * </ul>
 * </p>
 */
public interface VariableInferenceService {

    /**
     * 推断指定数据中各列的类型.
     *
     * @param data          导入的原始数据（每行为字段名-值的映射）
     * @param sampleSize    用于推断的采样行数（0表示全量分析）
     * @return 字段名到推断类型的映射
     */
    Map<String, VariableType> inferTypes(List<Map<String, Object>> data, int sampleSize);

    /**
     * 根据字段名模式推断类型（不依赖数据）.
     *
     * @param fieldName 字段名
     * @return 推断的VariableType，无法推断时返回null
     */
    VariableType inferTypeByFieldName(String fieldName);
}
