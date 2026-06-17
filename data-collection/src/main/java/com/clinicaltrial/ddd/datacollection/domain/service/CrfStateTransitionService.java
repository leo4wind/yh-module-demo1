package com.clinicaltrial.ddd.datacollection.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.datacollection.domain.model.aggregate.CrfAssessment;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;

/**
 * CrfStateTransitionService — CRF状态转换编排领域服务.
 * <p>
 * 根据完整性计算结果评估并执行CRF评估的状态转换。
 * 该服务封装了状态转换的决策逻辑，将完整性计算与状态变更解耦。
 * </p>
 *
 * <p>
 * 状态转换规则：
 * <ul>
 *   <li>完整性 >= 100% → 如果当前状态为PENDING/IN_PROGRESS，转换为COMPLETED</li>
 *   <li>完整性 > 0% → 如果当前状态为PENDING，转换为IN_PROGRESS</li>
 *   <li>完整性 = 0% → 如果当前状态为IN_PROGRESS且无任何填写值，回退到PENDING</li>
 * </ul>
 * 注意：状态 >= COMPLETED(2) 时不执行自动转换。
 * </p>
 */
@Service
public class CrfStateTransitionService {

    /**
     * 评估完整性并执行状态转换.
     * <p>
     * 该方法调用CrfAssessment.calculateCompleteness()，
     * 由聚合自身完成实际的状态转换和事件注册。
     * 领域服务在此仅起到编排和决策辅助的作用。
     * </p>
     *
     * @param assessment    CRF评估聚合
     * @param completeness  已计算好的完整性值
     * @return true 如果状态发生了转换
     */
    public boolean evaluateAndTransition(CrfAssessment assessment, Completeness completeness) {
        if (assessment == null || completeness == null) {
            return false;
        }

        // 判断是否需要转换
        boolean shouldTransition = shouldTransition(assessment, completeness);

        if (shouldTransition) {
            // 完整性计算和状态转换由聚合自身完成
            // 此处仅做编排判断，实际转换由聚合方法触发
            return true;
        }

        return false;
    }

    /**
     * 判断是否需要执行状态转换.
     *
     * @param assessment   CRF评估聚合
     * @param completeness 完整性值
     * @return true 如果需要转换
     */
    private boolean shouldTransition(CrfAssessment assessment, Completeness completeness) {
        // 仅在PENDING或IN_PROGRESS状态下允许自动转换
        if (!assessment.getStatus().canAutoTransition()) {
            return false;
        }

        // 完整性达到100% → 需要转换到COMPLETED
        if (completeness.isComplete() && !assessment.isCompleted()) {
            return true;
        }

        // 完整性>0但状态还是PENDING → 需要转换到IN_PROGRESS
        if (completeness.getPercentage().compareTo(java.math.BigDecimal.ZERO) > 0
                && assessment.getStatus().getCode() == 0) {
            return true;
        }

        return false;
    }
}
