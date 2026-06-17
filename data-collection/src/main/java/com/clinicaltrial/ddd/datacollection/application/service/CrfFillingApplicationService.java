package com.clinicaltrial.ddd.datacollection.application.service;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.datacollection.application.command.SaveCrfFieldValueCommand;
import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfFieldValueId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageStatus;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.datacollection.domain.repository.SubjectStageRepository;
import com.clinicaltrial.ddd.datacollection.domain.service.StageConfigurationProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * CrfFillingApplicationService — CRF填写应用服务.
 * <p>
 * 编排CRF字段值保存的完整用例流程：
 * <ol>
 *   <li>保存字段值到CrfAssessment聚合</li>
 *   <li>计算完整性并执行自动状态转换</li>
 *   <li>持久化聚合</li>
 *   <li>发布领域事件</li>
 *   <li>如果CRF评估完成，检查所属阶段是否可完成</li>
 * </ol>
 * 应用服务仅负责编排，所有业务规则和状态变更均在领域层聚合中执行。
 * </p>
 */
@Service
public class CrfFillingApplicationService {

    private static final Logger log = LoggerFactory.getLogger(CrfFillingApplicationService.class);

    private final CrfAssessmentRepository crfAssessmentRepository;
    private final SubjectStageRepository subjectStageRepository;
    private final StageConfigurationProvider stageConfigurationProvider;
    private final EventBus eventBus;

    /**
     * 构造CrfFillingApplicationService.
     *
     * @param crfAssessmentRepository     CRF评估仓储
     * @param subjectStageRepository      受试者阶段仓储
     * @param stageConfigurationProvider  阶段配置提供者
     * @param eventBus                    事件总线
     */
    public CrfFillingApplicationService(CrfAssessmentRepository crfAssessmentRepository,
                                         SubjectStageRepository subjectStageRepository,
                                         StageConfigurationProvider stageConfigurationProvider,
                                         EventBus eventBus) {
        this.crfAssessmentRepository = crfAssessmentRepository;
        this.subjectStageRepository = subjectStageRepository;
        this.stageConfigurationProvider = stageConfigurationProvider;
        this.eventBus = eventBus;
    }

    /**
     * 保存CRF字段值.
     * <p>
     * 完整用例流程：
     * <ol>
     *   <li>加载CrfAssessment聚合</li>
     *   <li>从命令创建CrfFieldValue实体</li>
     *   <li>调用聚合saveFieldValue()方法（含状态验证）</li>
     *   <li>获取CRF模板字段定义</li>
     *   <li>调用聚合calculateCompleteness()自动计算完整性和状态转换</li>
     *   <li>保存聚合</li>
     *   <li>发布所有领域事件</li>
     *   <li>如果评估变为COMPLETED，检查阶段是否可完成</li>
     * </ol>
     * </p>
     *
     * @param command 保存字段值命令
     */
    @Transactional
    public void saveFieldValue(SaveCrfFieldValueCommand command) {
        CrfAssessmentId assessmentId = command.getAssessmentId();
        log.info("Saving field value: assessment={}, fieldCode={}",
                assessmentId, command.getFieldCode());

        // 1. 加载CrfAssessment聚合
        CrfAssessment assessment = crfAssessmentRepository.getById(assessmentId);

        // 2. 创建CrfFieldValue实体
        CrfFieldValueId fieldValueId = new CrfFieldValueId(generateNumericId());
        CrfFieldValue fieldValue = new CrfFieldValue(
                fieldValueId,
                assessmentId,
                command.getFieldCode(),
                command.getFieldLabel(),
                command.getFieldValue(),
                command.getFieldValueText(),
                command.getDataUnit(),
                command.getFieldType(),
                command.getSubTableId(),
                null // sortNumber由仓储层分配
        );

        // 3. 保存字段值（聚合内验证状态）
        assessment.saveFieldValue(fieldValue, command.getUserId());

        // 4. 获取CRF模板字段定义
        CrfTemplateId crfId = assessment.getCrfId();
        CrfVersionId crfVersionId = assessment.getCrfVersionId();
        List<CrfField> templateFields =
                stageConfigurationProvider.findCrfTemplateFields(crfId, crfVersionId);
        if (templateFields == null || templateFields.isEmpty()) {
            templateFields = Collections.singletonList(
                    new CrfField(command.getFieldCode(), true, false, false));
        }

        // 5. 计算完整性和自动状态转换
        assessment.calculateCompleteness(templateFields);

        // 6. 保存聚合
        crfAssessmentRepository.save(assessment);

        // 7. 发布领域事件
        eventBus.publishAll(assessment);

        log.info("Field value saved: assessment={}, fieldCode={}, status={}, completeness={}",
                assessmentId, command.getFieldCode(),
                assessment.getStatus(), assessment.getCompleteness());

        // 8. 如果评估变为COMPLETED，检查阶段完成状态
        if (assessment.isCompleted()) {
            checkAndCompleteStage(assessment.getSubjectsStageId(), command.getUserId());
        }
    }

    /**
     * 检查并完成受试者阶段.
     * <p>
     * 当某个CRF评估完成后，检查所属阶段的所有CRF评估是否都已完成。
     * 如果全部完成，则调用SubjectStage.complete()完成阶段。
     * </p>
     *
     * @param subjectStageId 受试者阶段ID
     * @param userId         操作用户ID
     */
    private void checkAndCompleteStage(SubjectStageId subjectStageId, Long userId) {
        // 加载SubjectStage聚合
        SubjectStage stage = subjectStageRepository.getById(subjectStageId);

        // 如果阶段已不是IN_PROGRESS状态，跳过
        if (stage.getStatus() != SubjectStageStatus.IN_PROGRESS) {
            return;
        }

        // 获取该阶段下所有的CRF评估
        List<CrfAssessment> assessments =
                crfAssessmentRepository.findBySubjectsStageId(subjectStageId);

        // 收集已完成的评估ID集合
        Set<CrfAssessmentId> completedAssessmentIds = assessments.stream()
                .filter(CrfAssessment::isCompleted)
                .map(CrfAssessment::getId)
                .collect(Collectors.toSet());

        // 判断是否所有引用的CRF评估都已完成的依据是：
        // 已完成的评估包含了SubjectStage中引用的所有评估ID
        if (stage.isReadyToComplete(completedAssessmentIds)) {
            stage.complete(completedAssessmentIds, userId);
            subjectStageRepository.save(stage);
            eventBus.publishAll(stage);

            log.info("SubjectStage completed: stage={}, subject={}",
                    subjectStageId, stage.getSubjectsUserId());
        } else {
            log.info("SubjectStage not yet ready for completion: stage={}, " +
                            "completedRefs={}/{}",
                    subjectStageId, completedAssessmentIds.size(),
                    stage.getCrfAssessmentRefs().size());
        }
    }

    /**
     * 生成临时数值型ID.
     *
     * @return 正Long值
     */
    private Long generateNumericId() {
        return Math.abs(UUID.randomUUID().getMostSignificantBits());
    }
}
