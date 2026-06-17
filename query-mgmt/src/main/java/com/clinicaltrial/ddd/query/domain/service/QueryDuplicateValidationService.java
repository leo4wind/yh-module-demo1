package com.clinicaltrial.ddd.query.domain.service;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;

/**
 * QueryDuplicateValidationService — 质疑重复校验领域服务.
 * <p>
 * 校验同一CRF评估下同一字段是否已有未关闭的质疑，
 * 防止针对同一字段提出重复质疑。
 * </p>
 *
 * <p>
 * 业务规则：同一CRF评估的同一字段标识，不能同时存在多个状态为OPEN的质疑。
 * 如果检测到重复，抛出 {@link BusinessRuleViolationException}。
 * </p>
 */
public interface QueryDuplicateValidationService {

    /**
     * 校验指定CRF评估下指定字段标识是否已有未关闭的质疑.
     * <p>
     * 如果存在已OPEN的质疑，抛出BusinessRuleViolationException。
     * </p>
     *
     * @param assessmentId    CRF评估ID
     * @param fieldIdentifier 字段标识（包含fieldCode、subTableId、fieldType）
     * @throws BusinessRuleViolationException 如果该字段已有未关闭的质疑，
     *                                         错误信息："此记录已经质疑过了，在未回应质疑之前无法再次质疑"
     */
    void validateNoDuplicate(CrfAssessmentId assessmentId, QueryFieldIdentifier fieldIdentifier)
            throws BusinessRuleViolationException;
}
