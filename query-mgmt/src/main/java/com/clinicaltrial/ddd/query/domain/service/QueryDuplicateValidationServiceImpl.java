package com.clinicaltrial.ddd.query.domain.service;

import org.springframework.stereotype.Service;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.aggregate.Query;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.repository.QueryRepository;

import java.util.List;
import java.util.Objects;

/**
 * QueryDuplicateValidationServiceImpl — 质疑重复校验领域服务实现.
 * <p>
 * 校验同一CRF评估下同一字段是否已有未关闭的质疑，
 * 防止针对同一字段提出重复质疑。
 * </p>
 */
@Service
public class QueryDuplicateValidationServiceImpl implements QueryDuplicateValidationService {

    private final QueryRepository queryRepository;

    /**
     * 构造QueryDuplicateValidationServiceImpl.
     *
     * @param queryRepository Query仓储
     */
    public QueryDuplicateValidationServiceImpl(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public void validateNoDuplicate(CrfAssessmentId assessmentId, QueryFieldIdentifier fieldIdentifier)
            throws BusinessRuleViolationException {
        Objects.requireNonNull(assessmentId, "assessmentId must not be null");
        Objects.requireNonNull(fieldIdentifier, "fieldIdentifier must not be null");

        List<Query> existingQueries = queryRepository.findByAssessmentId(assessmentId);

        boolean hasDuplicate = existingQueries.stream()
                .anyMatch(q -> q.getStatus() == QueryStatus.OPEN
                        && fieldIdentifier.equals(q.getFieldIdentifier()));

        if (hasDuplicate) {
            throw new BusinessRuleViolationException(
                    "DUPLICATE_QUERY",
                    "此记录已经质疑过了，在未回应质疑之前无法再次质疑");
        }
    }
}
