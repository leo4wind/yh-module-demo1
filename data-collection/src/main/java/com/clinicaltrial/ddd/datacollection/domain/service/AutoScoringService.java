package com.clinicaltrial.ddd.datacollection.domain.service;

import com.clinicaltrial.ddd.common.infrastructure.EventBus;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfCompletedEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import java.math.BigDecimal;
import java.util.Map;

/**
 * AutoScoringService — CRF自动评分领域服务.
 * <p>
 * 监听CrfCompletedEvent（CRF评估完成事件），
 * 当CRF评估完整性达到100%时自动执行评分计算。
 * 评分规则由ScoringRuleProvider从外部获取。
 * </p>
 *
 * <p>
 * 工作流程：
 * <ol>
 *   <li>收到CrfCompletedEvent</li>
 *   <li>加载对应的CrfAssessment聚合</li>
 *   <li>通过ScoringRuleProvider获取评分规则</li>
 *   <li>调用聚合的autoScore()方法执行评分</li>
 *   <li>保存聚合并发布事件</li>
 * </ol>
 * </p>
 */
public class AutoScoringService {

    private static final Logger log = LoggerFactory.getLogger(AutoScoringService.class);

    private final CrfAssessmentRepository crfAssessmentRepository;
    private final ScoringRuleProvider scoringRuleProvider;
    private final EventBus eventBus;

    /**
     * 构造AutoScoringService.
     *
     * @param crfAssessmentRepository CRF评估仓储
     * @param scoringRuleProvider     评分规则提供者
     * @param eventBus                事件总线
     */
    public AutoScoringService(CrfAssessmentRepository crfAssessmentRepository,
                               ScoringRuleProvider scoringRuleProvider,
                               EventBus eventBus) {
        this.crfAssessmentRepository = crfAssessmentRepository;
        this.scoringRuleProvider = scoringRuleProvider;
        this.eventBus = eventBus;
    }

    /**
     * 处理CRF评估完成事件，执行自动评分.
     *
     * @param event CRF评估完成事件
     */
    @EventListener
    public void handleCrfCompleted(CrfCompletedEvent event) {
        CrfAssessmentId assessmentId = event.getAssessmentId();

        log.info("Handling CrfCompletedEvent: assessment={}", assessmentId);

        try {
            // 加载CRF评估聚合
            CrfAssessment assessment = crfAssessmentRepository.getById(assessmentId);

            // 获取评分规则
            CrfTemplateId crfId = assessment.getCrfId();
            CrfVersionId crfVersionId = assessment.getCrfVersionId();
            Map<String, BigDecimal> scoringRules =
                    scoringRuleProvider.findScoringRules(crfId, crfVersionId);

            // 执行自动评分
            assessment.autoScore(scoringRules);

            // 保存聚合
            crfAssessmentRepository.save(assessment);

            // 发布领域事件（如有）
            eventBus.publishAll(assessment);

            log.info("Auto-scored assessment={} with {} rules, score={}",
                    assessmentId,
                    scoringRules != null ? scoringRules.size() : 0,
                    assessment.getAssessmentScore());

        } catch (Exception e) {
            log.error("Failed to auto-score assessment={}", assessmentId, e);
        }
    }
}
