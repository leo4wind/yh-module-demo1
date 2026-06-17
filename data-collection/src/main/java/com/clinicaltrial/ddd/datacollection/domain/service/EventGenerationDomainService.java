package com.clinicaltrial.ddd.datacollection.domain.service;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.BaselineTime;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.datacollection.domain.repository.SubjectStageRepository;
import com.clinicaltrial.ddd.subject.domain.event.SubjectEnrolledEvent;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * EventGenerationDomainService — 事件驱动的阶段/评估生成领域服务.
 * <p>
 * 监听SubjectEnrolledEvent（受试者入组事件），
 * 自动为受试者生成所有需要自动添加的试验阶段（SubjectStage），
 * 并为每个阶段生成对应的CRF评估（CrfAssessment）。
 * </p>
 */
@Service
public class EventGenerationDomainService {

    private static final Logger log = LoggerFactory.getLogger(EventGenerationDomainService.class);

    private final SubjectStageRepository subjectStageRepository;
    private final CrfAssessmentRepository crfAssessmentRepository;
    private final StageConfigurationProvider stageConfigurationProvider;
    private final EventBus eventBus;

    /**
     * 构造EventGenerationDomainService.
     *
     * @param subjectStageRepository     受试者阶段仓储
     * @param crfAssessmentRepository    CRF评估仓储
     * @param stageConfigurationProvider 阶段配置提供者
     * @param eventBus                   事件总线
     */
    public EventGenerationDomainService(SubjectStageRepository subjectStageRepository,
                                         CrfAssessmentRepository crfAssessmentRepository,
                                         StageConfigurationProvider stageConfigurationProvider,
                                         EventBus eventBus) {
        this.subjectStageRepository = subjectStageRepository;
        this.crfAssessmentRepository = crfAssessmentRepository;
        this.stageConfigurationProvider = stageConfigurationProvider;
        this.eventBus = eventBus;
    }

    /**
     * 处理受试者入组事件，生成阶段和CRF评估.
     *
     * @param event 受试者入组事件
     */
    @EventListener
    public void handleSubjectEnrolled(SubjectEnrolledEvent event) {
        SubjectId subjectId = event.getSubjectId();
        Long projectId = event.getProjectId().getValue();

        log.info("Handling SubjectEnrolledEvent: subject={}, project={}", subjectId, projectId);

        // 1. 获取项目下所有自动添加的阶段
        List<StageId> autoAddStages = stageConfigurationProvider.findAutoAddStageIds(projectId);

        if (autoAddStages == null || autoAddStages.isEmpty()) {
            log.info("No auto-add stages configured for project {}", projectId);
            return;
        }

        // 2. 为每个自动添加的阶段创建SubjectStage和CrfAssessment
        for (StageId stageId : autoAddStages) {
            try {
                generateStageAndAssessments(subjectId, projectId, stageId);
            } catch (Exception e) {
                log.error("Failed to generate stage and assessments for subject={}, stage={}",
                        subjectId, stageId, e);
            }
        }
    }

    /**
     * 为指定受试者和阶段生成SubjectStage及所有关联的CrfAssessment.
     *
     * @param subjectId 受试者ID
     * @param projectId 项目ID
     * @param stageId   试验阶段ID
     */
    private void generateStageAndAssessments(SubjectId subjectId, Long projectId, StageId stageId) {
        // 生成唯一ID
        SubjectStageId subjectStageId = new SubjectStageId(generateNumericId());
        VisitPlanId planEventId = stageConfigurationProvider.findVisitPlanId(projectId, stageId);

        // 创建基线时间（入组日期作为基线）
        BaselineTime baselineTime = new BaselineTime(new Date(), "enrollment");

        // 创建SubjectStage
        SubjectStage subjectStage = SubjectStage.create(
                subjectStageId, subjectId, stageId, planEventId, baselineTime);

        // 获取阶段关联的CRF绑定
        Map<CrfTemplateId, CrfVersionId> crfBindings =
                stageConfigurationProvider.findStageCrfBindings(stageId);

        if (crfBindings != null) {
            for (Map.Entry<CrfTemplateId, CrfVersionId> binding : crfBindings.entrySet()) {
                CrfTemplateId crfId = binding.getKey();
                CrfVersionId crfVersionId = binding.getValue();

                // 创建CrfAssessment
                CrfAssessmentId assessmentId = new CrfAssessmentId(generateNumericId());
                CrfAssessment assessment = CrfAssessment.create(
                        assessmentId, subjectId, crfId, crfVersionId, subjectStageId);

                // 保存CRF评估
                crfAssessmentRepository.save(assessment);

                // 将评估引用添加到阶段
                subjectStage.addCrfAssessmentRef(assessmentId);
            }
        }

        // 保存受试者阶段
        subjectStageRepository.save(subjectStage);

        // 发布所有领域事件
        eventBus.publishAll(subjectStage);

        log.info("Generated stage={} with {} assessments for subject={}",
                subjectStageId, crfBindings != null ? crfBindings.size() : 0, subjectId);
    }

    /**
     * 生成临时数值型ID（生产环境中应由ID生成服务替换）.
     *
     * @return 正Long值
     */
    private Long generateNumericId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
