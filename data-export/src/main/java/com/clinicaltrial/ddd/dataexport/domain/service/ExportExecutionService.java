package com.clinicaltrial.ddd.dataexport.domain.service;

import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportResult;

/**
 * ExportExecutionService — 导出执行领域服务.
 * <p>
 * 负责执行数据导出的核心逻辑，通过策略模式委托给具体的导出器实现。
 * 协调数据查询、格式转换和文件生成的完整流程。
 * </p>
 *
 * <p>
 * 职责：
 * <ul>
 *   <li>根据导出任务配置获取数据</li>
 *   <li>选择合适的导出器执行格式转换</li>
 *   <li>生成导出文件并返回结果信息</li>
 * </ul>
 * </p>
 */
public interface ExportExecutionService {

    /**
     * 执行数据导出.
     *
     * @param exportTask 导出任务聚合
     * @return ExportResult 包含文件URL、文件名、记录数和文件大小
     */
    ExportResult executeExport(ExportTask exportTask);
}
