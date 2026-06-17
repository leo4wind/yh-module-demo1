package com.clinicaltrial.ddd.dataexport.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.dataexport.domain.model.aggregate.ExportTask;

/**
 * ExportRetryServiceImpl — 导出重试领域服务实现.
 */
@Service
public class ExportRetryServiceImpl implements ExportRetryService {

    @Override
    public void prepareRetry(ExportTask exportTask) throws BusinessRuleViolationException {
        if (!canRetry(exportTask)) {
            throw new BusinessRuleViolationException(
                    "EXPORT_MAX_RETRY_EXCEEDED",
                    "Export task has failed " + exportTask.getFailCount()
                            + " times, max retry count is " + MAX_RETRY_COUNT);
        }
    }

    @Override
    public boolean canRetry(ExportTask exportTask) {
        return exportTask.canRetry();
    }
}
