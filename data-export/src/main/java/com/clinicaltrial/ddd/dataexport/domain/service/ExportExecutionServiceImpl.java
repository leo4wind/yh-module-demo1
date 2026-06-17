package com.clinicaltrial.ddd.dataexport.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;
import com.clinicaltrial.ddd.dataexport.domain.model.valueobject.ExportResult;

/**
 * ExportExecutionServiceImpl — 导出执行领域服务实现.
 * <p>
 * TODO: 需要注入具体的导出器策略来执行实际的数据导出和文件生成。
 * 当前为占位实现，标记为待完善。
 * </p>
 */
@Service
public class ExportExecutionServiceImpl implements ExportExecutionService {

    @Override
    public ExportResult executeExport(ExportTask exportTask) {
        // TODO: Implement actual export logic with proper exporter strategy
        throw new UnsupportedOperationException(
                "Export execution not yet implemented. Task: " + exportTask.getTaskName());
    }
}
