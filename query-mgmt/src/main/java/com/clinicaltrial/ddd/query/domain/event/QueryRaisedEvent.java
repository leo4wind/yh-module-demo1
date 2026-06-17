package com.clinicaltrial.ddd.query.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * QueryRaisedEvent — 质疑提出事件.
 * <p>
 * 当监查员或稽查员对CRF评估中的某个字段提出质疑时触发。
 * 该事件会触发以下流程：
 * <ul>
 *   <li>更新CrfAssessment的状态为QUERIED</li>
 *   <li>通知研究者有新的质疑需要回应</li>
 *   <li>记录质疑审计日志</li>
 * </ul>
 * </p>
 */
public class QueryRaisedEvent implements DomainEvent {

    private final QueryId queryId;
    private final CrfAssessmentId assessmentId;
    private final QueryFieldIdentifier fieldIdentifier;
    private final LocalDateTime occurredOn;

    /**
     * 构造QueryRaisedEvent.
     *
     * @param queryId         质疑ID
     * @param assessmentId    被质疑的CRF评估ID
     * @param fieldIdentifier 被质疑的字段标识
     */
    public QueryRaisedEvent(QueryId queryId, CrfAssessmentId assessmentId,
                            QueryFieldIdentifier fieldIdentifier) {
        this.queryId = queryId;
        this.assessmentId = assessmentId;
        this.fieldIdentifier = fieldIdentifier;
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

    /**
     * 获取被质疑的字段标识.
     *
     * @return QueryFieldIdentifier
     */
    public QueryFieldIdentifier getFieldIdentifier() {
        return fieldIdentifier;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "QueryRaised: query " + queryId
                + " on assessment " + assessmentId
                + " field " + fieldIdentifier.getFieldCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryRaisedEvent that = (QueryRaisedEvent) o;
        return Objects.equals(queryId, that.queryId)
                && Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(fieldIdentifier, that.fieldIdentifier)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryId, assessmentId, fieldIdentifier, occurredOn);
    }

    @Override
    public String toString() {
        return "QueryRaisedEvent{"
                + "queryId=" + queryId
                + ", assessmentId=" + assessmentId
                + ", fieldIdentifier=" + fieldIdentifier
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
