package com.clinicaltrial.ddd.query.domain.service;

import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.MonitoringStatus;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.query.domain.event.QueryClosedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryRaisedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryReopenedEvent;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;
import org.springframework.context.event.EventListener;

import java.util.Objects;

/**
 * CrfStatusSynchronizationService — CRF评估状态同步服务.
 * <p>
 * 领域事件监听器，负责在质疑生命周期事件发生时同步更新CrfAssessment的监控状态.
 * </p>
 */
public class CrfStatusSynchronizationService {

    private final CrfAssessmentRepository assessmentRepository;
    private final QueryRepository queryRepository;

    /**
     * 构造CrfStatusSynchronizationService.
     *
     * @param assessmentRepository CrfAssessment仓储
     * @param queryRepository      Query仓储
     */
    public CrfStatusSynchronizationService(CrfAssessmentRepository assessmentRepository,
                                            QueryRepository queryRepository) {
        this.assessmentRepository = assessmentRepository;
        this.queryRepository = queryRepository;
    }

    /**
     * 监听质疑提出事件，同步CrfAssessment状态为QUERIED.
     * <p>
     * 当新的质疑被提出时，将关联的CRF评估状态从COMPLETED转为QUERIED。
     * 如果评估已被质疑（状态已经是QUERIED），则无需重复转换。
     * </p>
     *
     * @param event 质疑提出事件
     */
    @EventListener
    public void onQueryRaised(QueryRaisedEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        CrfAssessment assessment = assessmentRepository.getById(event.getAssessmentId());
        // raiseQuery() 要求状态为COMPLETED，如果评估已经在QUERIED状态
        //（已有其他开放质疑），跳过转换避免业务规则异常
        if (assessment.getStatus() != MonitoringStatus.QUERIED) {
            assessment.raiseQuery();
            assessmentRepository.save(assessment);
        }
        // 如果状态已经是QUERIED，说明已有其他质疑，无需重复转换
    }

    /**
     * 监听质疑关闭事件，检查是否需要恢复CrfAssessment状态为COMPLETED.
     * <p>
     * 当质疑被关闭后，检查该CRF评估下是否还有未关闭的质疑。
     * 如果没有，则调用 {@link CrfAssessment#resolveAllQueries()} 将状态恢复为COMPLETED。
     * </p>
     *
     * @param event 质疑关闭事件
     */
    @EventListener
    public void onQueryClosed(QueryClosedEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        CrfAssessment assessment = assessmentRepository.getById(event.getAssessmentId());
        // 如果状态已是COMPLETED（已通过应用服务的同步流程处理完毕），跳过
        if (assessment.getStatus() != MonitoringStatus.QUERIED) {
            return;
        }
        int openCount = queryRepository.countOpenQueriesByAssessmentId(event.getAssessmentId());
        if (openCount <= 0) {
            assessment.resolveAllQueries();
            assessmentRepository.save(assessment);
        }
    }

    /**
     * 监听质疑重新打开事件，同步CrfAssessment状态为QUERIED.
     * <p>
     * 当已关闭的质疑被重新打开时，将关联的CRF评估状态再次从COMPLETED转为QUERIED。
     * </p>
     *
     * @param event 质疑重新打开事件
     */
    @EventListener
    public void onQueryReopened(QueryReopenedEvent event) {
        Objects.requireNonNull(event, "event must not be null");

        CrfAssessment assessment = assessmentRepository.getById(event.getAssessmentId());
        // 如果评估不在QUERIED状态（可能已被其他质疑重新打开），才需要转换
        if (assessment.getStatus() != MonitoringStatus.QUERIED) {
            assessment.raiseQuery();
            assessmentRepository.save(assessment);
        }
    }
}
