package com.clinicaltrial.ddd.statistics.domain.service;

import com.clinicaltrial.ddd.statistics.domain.model.entity.AnalysisResult;
import com.clinicaltrial.ddd.statistics.domain.model.valueobject.AnalysisResultId;

import java.util.List;

/**
 * ResultManagementService — 分析结果管理领域服务.
 * <p>
 * 负责分析结果的生命周期管理，包括结果检索、排序、筛选、
 * 收藏管理和历史版本管理。
 * </p>
 *
 * <p>
 * 职责：
 * <ul>
 *   <li>检索分析项目中的分析结果</li>
 *   <li>管理结果的收藏状态</li>
 *   <li>生成结果摘要</li>
 *   <li>比较不同分析结果</li>
 * </ul>
 * </p>
 */
public interface ResultManagementService {

    /**
     * 获取分析项目的收藏结果列表.
     *
     * @param results 分析项目中的全部结果列表
     * @return 仅包含收藏的结果列表
     */
    List<AnalysisResult> getFavorites(List<AnalysisResult> results);

    /**
     * 切换结果的收藏状态.
     *
     * @param result 分析结果实体
     */
    void toggleFavorite(AnalysisResult result);

    /**
     * 为分析结果生成文本摘要.
     *
     * @param result 分析结果实体
     * @return 摘要文本
     */
    String generateSummary(AnalysisResult result);
}
