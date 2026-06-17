package com.clinicaltrial.ddd.query.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * QueryRespondedEvent — 质疑回应事件.
 * <p>
 * 当研究者对质疑作出回应（包括文字说明或修改字段值）时触发。
 * 该事件表示研究者已处理质疑，等待监查员审核并关闭。
 * </p>
 */
public class QueryRespondedEvent implements DomainEvent {

    private final QueryId queryId;
    private final CrfAssessmentId assessmentId;
    private final LocalDateTime occurredOn;

    /**
     * 构造QueryRespondedEvent.
     *
     * @param queryId       质疑ID
     * @param assessmentId  被质疑的CRF评估ID
     */
    public QueryRespondedEvent(QueryId queryId, CrfAssessmentId assessmentId) {
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
        return "QueryResponded: query " + queryId
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
        QueryRespondedEvent that = (QueryRespondedEvent) o;
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
        return "QueryRespondedEvent{"
                + "queryId=" + queryId
                + ", assessmentId=" + assessmentId
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
