package com.clinicaltrial.ddd.query.domain.event;

import com.clinicaltrial.ddd.common.model.DomainEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * QueryReopenedEvent — 质疑重新打开事件.
 * <p>
 * 当已关闭的质疑因新的原因被重新打开时触发。
 * 该事件会触发CrfAssessment的状态再次从COMPLETED转为QUERIED，
 * 表示该CRF评估仍有待处理的质疑。
 * </p>
 */
public class QueryReopenedEvent implements DomainEvent {

    private final QueryId queryId;
    private final CrfAssessmentId assessmentId;
    private final String reason;
    private final LocalDateTime occurredOn;

    /**
     * 构造QueryReopenedEvent.
     *
     * @param queryId       质疑ID
     * @param assessmentId  被质疑的CRF评估ID
     * @param reason        重新打开的原因
     */
    public QueryReopenedEvent(QueryId queryId, CrfAssessmentId assessmentId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("reason must not be null or empty");
        }
        this.queryId = queryId;
        this.assessmentId = assessmentId;
        this.reason = reason;
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
     * 获取重新打开的原因.
     *
     * @return 原因描述
     */
    public String getReason() {
        return reason;
    }

    @Override
    public LocalDateTime occurredOn() {
        return occurredOn;
    }

    @Override
    public String description() {
        return "QueryReopened: query " + queryId
                + " on assessment " + assessmentId
                + ", reason: " + reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryReopenedEvent that = (QueryReopenedEvent) o;
        return Objects.equals(queryId, that.queryId)
                && Objects.equals(assessmentId, that.assessmentId)
                && Objects.equals(reason, that.reason)
                && Objects.equals(occurredOn, that.occurredOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queryId, assessmentId, reason, occurredOn);
    }

    @Override
    public String toString() {
        return "QueryReopenedEvent{"
                + "queryId=" + queryId
                + ", assessmentId=" + assessmentId
                + ", reason='" + reason + '\''
                + ", occurredOn=" + occurredOn
                + '}';
    }
}
