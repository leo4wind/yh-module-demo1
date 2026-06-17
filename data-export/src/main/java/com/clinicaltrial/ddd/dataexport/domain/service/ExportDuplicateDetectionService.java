package com.clinicaltrial.ddd.dataexport.domain.service;

import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportTaskId;

/**
 * ExportDuplicateDetectionService — 导出重复检测领域服务.
 * <p>
 * 校验是否存在重复的导出任务，防止针对相同数据范围（项目、阶段、CRF版本）
 * 和文件格式创建多个雷同的导出任务。
 * </p>
 *
 * <p>
 * 业务规则：同一项目下不能创建相同名称的导出任务。
 * 如果检测到重复，抛出 {@link BusinessRuleViolationException}。
 * </p>
 */
public interface ExportDuplicateDetectionService {

    /**
     * 校验指定任务名称在同一项目中是否已存在.
     *
     * @param taskName   任务名称
     * @param projectId  项目ID（字符串形式）
     * @throws BusinessRuleViolationException 如果已存在同名导出任务
     */
    void validateTaskNameUnique(String taskName, String projectId)
            throws BusinessRuleViolationException;

    /**
     * 校验指定导出任务是否可以提交审批（排除自身的名称冲突）.
     *
     * @param taskId     当前导出任务ID（排除自身）
     * @param taskName   任务名称
     * @param projectId  项目ID（字符串形式）
     * @throws BusinessRuleViolationException 如果已存在同名导出任务（非自身）
     */
    void validateTaskNameUniqueExcludingSelf(ExportTaskId taskId, String taskName, String projectId)
            throws BusinessRuleViolationException;
}
