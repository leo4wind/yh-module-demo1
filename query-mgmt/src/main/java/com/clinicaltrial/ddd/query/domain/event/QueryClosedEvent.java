package com.clinicaltrial.ddd.query.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * QueryClosedEvent — 质疑关闭事件.
 * <p>
 * 当监查员接受研究者回应并关闭质疑时触发。
 * 该事件会触发CrfAssessment检查是否还有未关闭的质疑：
 * 如果该CRF评估下所有质疑均已关闭，则调用resolveAllQueries()
 * 将CrfAssessment状态从QUERIED恢复为COMPLETED。
 * </p>
 */
public class QueryClosedEvent implements DomainEvent {

    private final QueryId queryId;
    private final CrfAssessmentId assessmentId;
    private final LocalDateTime occurredOn;

    /**
     * 构造QueryClosedEvent.
     *
     * @param queryId       质疑ID
     * @param assessmentId  被质疑的CRF评估ID
     */
    public QueryClosedEvent(QueryId queryId, CrfAssessmentId assessmentId) {
        this.queryId = queryId;
        this.assessmentId = assessmentId;
        this.occurredOn = LocalDateTime.now();
    }

    /**
     * 获取质疑ID.
     *
     * @return QueryId
     */
    public QueryId getQueryId() {
        return queryId;
    }

    /**
     * 获取被质疑的CRF评估ID.
     *
     * @return CrfAssessmentId
     */
    public CrfAssessmentId getAssessmentId() {
        return assessmentId;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "QueryClosed: query " + queryId
                + " on assessment " + assessmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryClosedEvent that = (QueryClosedEvent) o;
        return Objects.equals(queryId, that.queryId)
                && Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryId, assessmentId, occurredOn);
    }

    @Override
    public String toString() {
        return "QueryClosedEvent{"
                + "queryId=" + queryId
                + ", assessmentId=" + assessmentId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
