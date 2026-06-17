package com.clinicaltrial.ddd.datacollection.application.service;

import com.clinicaltrial.ddd.datacollection.application.command.TriggerDataExtractionCommand;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.SubjectStage;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.datacollection.domain.repository.SubjectStageRepository;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * DataExtractionApplicationService — 数据抽取应用服务.
 * <p>
 * 负责从外部系统（HIS/LIS等）抽取受试者的临床数据，
 * 并自动填充到对应的CRF评估记录中。
 * </p>
 *
 * <p>
 * 典型用例：
 * <ol>
 *   <li>触发某个受试者某个阶段的数据抽取</li>
 *   <li>系统从HIS/LIS系统拉取检查结果、检验数据等</li>
 *   <li>将数据自动填充到对应的CRF评估字段中</li>
 *   <li>更新CRF评估的完整性和状态</li>
 * </ol>
 * </p>
 */
@Service
public class DataExtractionApplicationService {

    private static final Logger log = LoggerFactory.getLogger(DataExtractionApplicationService.class);

    private final SubjectStageRepository subjectStageRepository;
    private final CrfAssessmentRepository crfAssessmentRepository;
    private final CrfFillingApplicationService crfFillingApplicationService;

    /**
     * 构造DataExtractionApplicationService.
     *
     * @param subjectStageRepository     受试者阶段仓储
     * @param crfAssessmentRepository    CRF评估仓储
     * @param crfFillingApplicationService CRF填写应用服务（用于填充抽取的数据）
     */
    public DataExtractionApplicationService(SubjectStageRepository subjectStageRepository,
                                              CrfAssessmentRepository crfAssessmentRepository,
                                              CrfFillingApplicationService crfFillingApplicationService) {
        this.subjectStageRepository = subjectStageRepository;
        this.crfAssessmentRepository = crfAssessmentRepository;
        this.crfFillingApplicationService = crfFillingApplicationService;
    }

    /**
     * 执行数据抽取并填充到CRF评估中.
     * <p>
     * 根据命令参数，查找指定受试者和阶段的SubjectStage，
     * 对每个CRF评估执行数据抽取逻辑。
     * 实际的数据映射逻辑由具体的外部系统适配器实现。
     * </p>
     *
     * @param command 数据抽取触发命令
     */
    @Transactional
    public void extractAndFill(TriggerDataExtractionCommand command) {
        SubjectId subjectId = command.getSubjectsUserId();
        StageId stageId = command.getStageId();
        Long userId = command.getUserId();

        log.info("Starting data extraction: subject={}, stage={}", subjectId, stageId);

        // 1. 查找受试者阶段
        SubjectStage stage = subjectStageRepository
                .findBySubjectIdAndStageId(subjectId, stageId)
                .orElseThrow(() -> new IllegalStateException(
                        "SubjectStage not found for subject " + subjectId + " and stage " + stageId));

        SubjectStageId subjectStageId = stage.getId();

        // 2. 获取该阶段下的所有CRF评估
        List<CrfAssessment> assessments =
                crfAssessmentRepository.findBySubjectsStageId(subjectStageId);

        if (assessments == null || assessments.isEmpty()) {
            log.warn("No CRF assessments found for subjectStage={}", subjectStageId);
            return;
        }

        // 3. 对每个CRF评估执行数据抽取
        for (CrfAssessment assessment : assessments) {
            try {
                extractAndFillAssessment(assessment, subjectId, userId);
            } catch (Exception e) {
                log.error("Failed to extract data for assessment={}", assessment.getId(), e);
            }
        }

        log.info("Data extraction completed: subject={}, stage={}, assessments={}",
                subjectId, stageId, assessments.size());
    }

    /**
     * 对单个CRF评估执行数据抽取.
     * <p>
     * 调用外部系统适配器获取数据，然后通过CrfFillingApplicationService
     * 将数据填充到CRF评估中。
     * </p>
     *
     * @param assessment CRF评估聚合
     * @param subjectId  受试者ID
     * @param userId     操作用户ID
     */
    private void extractAndFillAssessment(CrfAssessment assessment,
                                           SubjectId subjectId, Long userId) {
        CrfTemplateId crfId = assessment.getCrfId();
        CrfVersionId crfVersionId = assessment.getCrfVersionId();

        // 此处应由具体的数据抽取适配器实现
        // 例如：从HIS系统获取实验室检查数据
        // 从LIS系统获取检验报告数据
        // 从外部EDC系统获取已有数据

        log.info("Extracting data for assessment={}, crfId={}, versionId={}",
                assessment.getId(), crfId, crfVersionId);

        // 实际实现中，此处会：
        // 1. 调用外部系统API获取数据（通过适配器/端口）
        // 2. 将数据转换为SaveCrfFieldValueCommand
        // 3. 调用crfFillingApplicationService.saveFieldValue()填充数据

        // 具体的数据映射逻辑由基础设施层实现
        // 本服务仅提供编排和事务管理
    }
}
