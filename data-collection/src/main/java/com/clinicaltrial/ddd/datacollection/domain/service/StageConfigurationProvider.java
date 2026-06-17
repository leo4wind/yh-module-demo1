package com.clinicaltrial.ddd.datacollection.domain.service;

import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;

import java.util.List;
import java.util.Map;

/**
 * StageConfigurationProvider — 阶段配置提供者接口（六边形架构端口）.
 * <p>
 * 定义从BC1（试验配置）获取阶段和CRF配置所需的操作。
 * 由基础设施层实现，领域层仅依赖此接口。
 * </p>
 */
public interface StageConfigurationProvider {

    /**
     * 获取项目下所有自动添加的阶段ID列表.
     *
     * @param projectId 项目ID（使用受试者管理上下文中的Long值）
     * @return 自动添加的阶段ID列表
     */
    List<StageId> findAutoAddStageIds(Long projectId);

    /**
     * 获取指定阶段的CRF绑定列表（StageCrfBinding）.
     * <p>
     * 返回Map：key为CRF模板ID，value为CRF版本ID。
     * </p>
     *
     * @param stageId 阶段ID
     * @return CRF模板ID → CRF版本ID的映射
     */
    Map<CrfTemplateId, CrfVersionId> findStageCrfBindings(StageId stageId);

    /**
     * 获取CRF模板的字段定义列表.
     *
     * @param crfId      CRF模板ID
     * @param crfVersionId CRF版本ID
     * @return 字段定义列表
     */
    List<CrfField> findCrfTemplateFields(CrfTemplateId crfId, CrfVersionId crfVersionId);

    /**
     * 获取访视计划ID（根据阶段生成事件）.
     *
     * @param projectId 项目ID
     * @param stageId   阶段ID
     * @return 访视计划ID
     */
    VisitPlanId findVisitPlanId(Long projectId, StageId stageId);
}
