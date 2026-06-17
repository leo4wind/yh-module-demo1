package com.clinicaltrial.ddd.dataexport.domain.service;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;

/**
 * ExportRetryService — 导出重试领域服务.
 * <p>
 * 管理导出任务的重试逻辑。
 * 当导出失败时判断是否允许重试，以及执行重试前的准备操作。
 * 最大重试次数为3次。
 * </p>
 *
 * <p>
 * 业务规则：
 * <ul>
 *   <li>失败次数达到3次后不允许再次重试</li>
 *   <li>重试前需要重置导出任务的状态和清理上次的失败信息</li>
 * </ul>
 * </p>
 */
public interface ExportRetryService {

    /**
     * 最大允许重试次数.
     */
    int MAX_RETRY_COUNT = 3;

    /**
     * 准备重试导出任务.
     * <p>
     * 校验失败次数是否允许重试，如果允许则标记为导出中状态。
     * </p>
     *
     * @param exportTask 导出任务聚合
     * @throws BusinessRuleViolationException 如果已达到最大重试次数
     */
    void prepareRetry(ExportTask exportTask) throws BusinessRuleViolationException;

    /**
     * 判断是否允许重试.
     *
     * @param exportTask 导出任务聚合
     * @return true 如果允许重试
     */
    boolean canRetry(ExportTask exportTask);
}
