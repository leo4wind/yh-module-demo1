package com.clinicaltrial.ddd.query.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.repository.CrfAssessmentRepository;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;

import java.util.Objects;

/**
 * QueryLifecycleService — 质疑生命周期领域服务.
 * <p>
 * 编排质疑状态转换与CRF评估状态同步的跨聚合业务逻辑。
 * </p>
 *
 * <p>
 * 该服务是无状态的。应用层负责提供已加载的CrfAssessment实例，
 * 服务负责执行领域的跨聚合校验和状态转换。
 * 应用层负责持久化两个聚合的变更。
 * </p>
 */
@Service
public class QueryLifecycleService {

    private final CrfAssessmentRepository assessmentRepository;
    private final QueryRepository queryRepository;

    /**
     * 构造QueryLifecycleService.
     *
     * @param assessmentRepository CrfAssessment仓储
     * @param queryRepository      Query仓储
     */
    public QueryLifecycleService(CrfAssessmentRepository assessmentRepository,
                                  QueryRepository queryRepository) {
        this.assessmentRepository = assessmentRepository;
        this.queryRepository = queryRepository;
    }

    /**
     * 提出质疑，同步CrfAssessment状态为QUERIED.
     * <p>
     * 调用 {@link CrfAssessment#raiseQuery()} 将评估状态从COMPLETED转为QUERIED。
     * </p>
     *
     * @param assessment 已加载的CrfAssessment实例
     * @throws BusinessRuleViolationException 如果CrfAssessment状态不允许质疑
     * @throws IllegalArgumentException 如果assessment为null
     */
    public void raiseQuery(CrfAssessment assessment) {
        Objects.requireNonNull(assessment, "assessment must not be null");
        assessment.raiseQuery();
    }

    /**
     * 关闭质疑后检查是否需要恢复CrfAssessment状态为COMPLETED.
     * <p>
     * 检查该评估下是否还有未关闭的质疑（不包括已持久化的当前Query）。
     * 如果所有质疑都已关闭，调用 {@link CrfAssessment#resolveAllQueries()}
     * 将评估状态从QUERIED恢复为COMPLETED。
     * </p>
     *
     * @param assessment          已加载的CrfAssessment实例
     * @param assessmentId        被质疑的CRF评估ID
     * @throws BusinessRuleViolationException 如果CrfAssessment状态不允许恢复
     * @throws IllegalArgumentException 如果参数为null
     */
    public void closeQuery(CrfAssessment assessment, CrfAssessmentId assessmentId) {
        Objects.requireNonNull(assessment, "assessment must not be null");
        Objects.requireNonNull(assessmentId, "assessmentId must not be null");

        // 查询当前未关闭的质疑数（此方法在query被持久化前调用，
        // 因此count包含当前正在被关闭的这条质疑）
        int openCount = queryRepository.countOpenQueriesByAssessmentId(assessmentId);

        // 如果只有当前这一条质疑（count为1，因为query尚未被持久化为CLOSED），
        // 或者没有其他未关闭质疑时，恢复评估状态
        if (openCount <= 1) {
            assessment.resolveAllQueries();
        }
    }

    /**
     * 获取CrfAssessment仓储（供事件监听器使用）.
     *
     * @return CrfAssessmentRepository
     */
    public CrfAssessmentRepository getAssessmentRepository() {
        return assessmentRepository;
    }

    /**
     * 获取Query仓储（供事件监听器使用）.
     *
     * @return QueryRepository
     */
    public QueryRepository getQueryRepository() {
        return queryRepository;
    }
}
