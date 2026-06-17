package com.clinicaltrial.ddd.datacollection.domain.service;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;

import java.math.BigDecimal;
import java.util.Map;

/**
 * ScoringRuleProvider — 评分规则提供者接口（六边形架构端口）.
 * <p>
 * 定义从外部获取CRF评分规则所需的操作。
 * 评分规则通常是字段编码到分值的映射。
 * 由基础设施层实现，领域层仅依赖此接口。
 * </p>
 */
public interface ScoringRuleProvider {

    /**
     * 获取指定CRF模板版本的评分规则.
     * <p>
     * 返回Map中key为字段编码（fieldCode），value为该字段的权重/分值。
     * 完成评估时，系统根据字段值是否符合规则计算总分。
     * </p>
     *
     * @param crfId      CRF模板ID
     * @param crfVersionId CRF版本ID
     * @return 字段编码 → 分值的映射，如果没有评分规则则返回空Map
     */
    Map<String, BigDecimal> findScoringRules(CrfTemplateId crfId, CrfVersionId crfVersionId);
}
