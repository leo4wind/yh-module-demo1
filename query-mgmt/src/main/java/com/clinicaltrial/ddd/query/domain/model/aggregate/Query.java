package com.clinicaltrial.ddd.query.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SnapshotValue;
import com.clinicaltrial.ddd.query.domain.event.QueryClosedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryRaisedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryReopenedEvent;
import com.clinicaltrial.ddd.query.domain.event.QueryRespondedEvent;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryFieldIdentifier;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryType;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryUpdateType;

import java.util.Date;
import java.util.Objects;

/**
 * Query — 质疑聚合根.
 * <p>
 * 表示对CRF评估中某个字段的数据质疑。质疑由监查员或稽查员提出，
 * 研究者回应后由监查员关闭。质疑可以重新打开。
 * </p>
 *
 * <h3>状态机</h3>
 * <pre>
 * OPEN(0) ──→ RESPONDED(1): 研究者回应质疑
 * OPEN(0) ──→ CLOSED(2):    监查员直接关闭质疑
 * RESPONDED(1) ──→ CLOSED(2): 监查员接受回应并关闭
 * CLOSED(2) ──→ OPEN(0):     重新打开已关闭的质疑
 * </pre>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>只有状态为OPEN的质疑可以被回应</li>
 *   <li>只有状态为OPEN或RESPONDED的质疑可以被关闭</li>
 *   <li>只有状态为CLOSED的质疑可以被重新打开</li>
 *   <li>同一CRF评估下同一字段不能有多个同时OPEN的质疑（由领域服务校验）</li>
 * </ul>
 *
 * @see QueryStatus
 * @see QueryType
 * @see QueryUpdateType
 */
public class Query extends AggregateRoot<QueryId> {

    private QueryId id;
    private CrfAssessmentId assessmentId;
    private QueryFieldIdentifier fieldIdentifier;
    private QueryStatus status;
    private QueryType type;
    private String question;
    private String response;
    private QueryUpdateType updateType;
    private Long createUserId;
    private Long updateUserId;
    private Date createTime;
    private Date updateTime;
    private SnapshotValue originalValue;
    private SnapshotValue currentValue;

    /**
     * 私有构造函数，通过工厂方法 {@link #raise} 创建.
     */
    private Query() {
    }

    // ========== 工厂方法 ==========

    /**
     * 提出质疑（工厂方法）.
     * <p>
     * 创建一条新的质疑记录，初始状态为OPEN(0)。
     * 自动注册 {@link QueryRaisedEvent} 事件。
     * </p>
     *
     * @param id               质疑ID
     * @param assessmentId     被质疑的CRF评估ID
     * @param fieldIdentifier  被质疑字段标识
     * @param type             质疑类型（监查质疑/稽查质疑）
     * @param question         质疑内容描述
     * @param originalValue    质疑时的字段值快照
     * @param userId           提出质疑的用户ID
     * @return 新建的Query实例
     * @throws IllegalArgumentException 如果任何必需参数为null
     */
    public static Query raise(QueryId id,
                               CrfAssessmentId assessmentId,
                               QueryFieldIdentifier fieldIdentifier,
                               QueryType type,
                               String question,
                               SnapshotValue originalValue,
                               Long userId) {
        Objects.requireNonNull(id, "QueryId must not be null");
        Objects.requireNonNull(assessmentId, "assessmentId must not be null");
        Objects.requireNonNull(fieldIdentifier, "fieldIdentifier must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(question, "question must not be null");
        Objects.requireNonNull(originalValue, "originalValue must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        if (question.trim().isEmpty()) {
            throw new IllegalArgumentException("question must not be empty");
        }

        Query query = new Query();
        query.id = id;
        query.assessmentId = assessmentId;
        query.fieldIdentifier = fieldIdentifier;
        query.status = QueryStatus.OPEN;
        query.type = type;
        query.question = question;
        query.originalValue = originalValue;
        query.currentValue = originalValue;
        query.createUserId = userId;
        query.updateUserId = userId;
        Date now = new Date();
        query.createTime = now;
        query.updateTime = now;

        query.registerEvent(new QueryRaisedEvent(id, assessmentId, fieldIdentifier));
        return query;
    }

    /**
     * 从持久化存储重建Query（不含副作用，不注册事件）.
     *
     * @param id               质疑ID
     * @param assessmentId     被质疑的CRF评估ID
     * @param fieldIdentifier  被质疑字段标识
     * @param status           质疑状态
     * @param type             质疑类型
     * @param question         质疑内容
     * @param response         研究者回应（可为null）
     * @param updateType       更新类型（可为null）
     * @param createUserId     创建用户ID
     * @param updateUserId     最后更新用户ID
     * @param createTime       创建时间
     * @param updateTime       最后更新时间
     * @param originalValue    质疑时原值快照
     * @param currentValue     当前值快照
     * @return 重建的Query实例
     */
    public static Query reconstruct(QueryId id,
                                     CrfAssessmentId assessmentId,
                                     QueryFieldIdentifier fieldIdentifier,
                                     QueryStatus status,
                                     QueryType type,
                                     String question,
                                     String response,
                                     QueryUpdateType updateType,
                                     Long createUserId,
                                     Long updateUserId,
                                     Date createTime,
                                     Date updateTime,
                                     SnapshotValue originalValue,
                                     SnapshotValue currentValue) {
        Query query = new Query();
        query.id = id;
        query.assessmentId = assessmentId;
        query.fieldIdentifier = fieldIdentifier;
        query.status = status;
        query.type = type;
        query.question = question;
        query.response = response;
        query.updateType = updateType;
        query.createUserId = createUserId;
        query.updateUserId = updateUserId;
        query.createTime = createTime;
        query.updateTime = updateTime;
        query.originalValue = originalValue;
        query.currentValue = currentValue;
        return query;
    }

    // ========== 业务方法（状态转换） ==========

    /**
     * 研究者回应质疑.
     * <p>
     * 状态转换：OPEN(0) → RESPONDED(1)。
     * 需要提供回应内容和当前字段值快照。
     * 注册 {@link QueryRespondedEvent} 事件。
     * </p>
     *
     * @param response      研究者回应内容
     * @param currentValue  回应时的当前字段值快照
     * @param userId        回应操作的用户ID
     * @throws BusinessRuleViolationException 如果当前状态不是OPEN
     * @throws IllegalArgumentException 如果response或currentValue为null
     */
    public void respond(String response, SnapshotValue currentValue, Long userId) {
        if (response == null || response.trim().isEmpty()) {
            throw new IllegalArgumentException("response must not be null or empty");
        }
        Objects.requireNonNull(currentValue, "currentValue must not be null");
        Objects.requireNonNull(userId, "userId must not be null");

        if (status != QueryStatus.OPEN) {
            throw new BusinessRuleViolationException(
                    "QUERY_INVALID_TRANSITION",
                    "Cannot respond to query " + id + ": current status is " + status
                            + ", expected OPEN");
        }

        this.response = response;
        this.currentValue = currentValue;
        this.updateType = QueryUpdateType.CLARIFY_ONLY;
        this.status = QueryStatus.RESPONDED;
        this.updateUserId = userId;
        this.updateTime = new Date();

        registerEvent(new QueryRespondedEvent(id, assessmentId));
    }

    /**
     * 关闭质疑（监查员接受回应或直接关闭）.
     * <p>
     * 状态转换：OPEN(0) → CLOSED(2) 或 RESPONDED(1) → CLOSED(2)。
     * 注册 {@link QueryClosedEvent} 事件。
     * </p>
     *
     * @param userId 关闭操作的用户ID
     * @throws BusinessRuleViolationException 如果当前状态不是OPEN或RESPONDED
     */
    public void close(Long userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        if (status != QueryStatus.OPEN && status != QueryStatus.RESPONDED) {
            throw new BusinessRuleViolationException(
                    "QUERY_INVALID_TRANSITION",
                    "Cannot close query " + id + ": current status is " + status
                            + ", expected OPEN or RESPONDED");
        }

        this.status = QueryStatus.CLOSED;
        this.updateUserId = userId;
        this.updateTime = new Date();

        registerEvent(new QueryClosedEvent(id, assessmentId));
    }

    /**
     * 重新打开已关闭的质疑.
     * <p>
     * 状态转换：CLOSED(2) → OPEN(0)。
     * 需要提供重新打开的理由。
     * 注册 {@link QueryReopenedEvent} 事件。
     * </p>
     *
     * @param reason 重新打开的原因说明
     * @param userId 重新打开操作的用户ID
     * @throws BusinessRuleViolationException 如果当前状态不是CLOSED
     * @throws IllegalArgumentException 如果reason为null或空
     */
    public void reopen(String reason, Long userId) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("reason must not be null or empty");
        }
        Objects.requireNonNull(userId, "userId must not be null");

        if (status != QueryStatus.CLOSED) {
            throw new BusinessRuleViolationException(
                    "QUERY_INVALID_TRANSITION",
                    "Cannot reopen query " + id + ": current status is " + status
                            + ", expected CLOSED");
        }

        this.status = QueryStatus.OPEN;
        this.question = reason;
        this.updateUserId = userId;
        this.updateTime = new Date();

        // 重置回应内容，因为质疑被重新打开需要新的回应
        this.response = null;
        this.updateType = null;

        registerEvent(new QueryReopenedEvent(id, assessmentId, reason));
    }

    /**
     * 设置回应更新类型（由应用层在回应后根据实际数据修改情况设置）.
     *
     * @param updateType 更新类型
     */
    public void setUpdateType(QueryUpdateType updateType) {
        this.updateType = updateType;
    }

    // ========== 查询方法 ==========

    /**
     * 判断质疑是否处于开放状态（等待回应）.
     *
     * @return true 如果状态为OPEN
     */
    public boolean isOpen() {
        return status == QueryStatus.OPEN;
    }

    /**
     * 判断质疑是否已被研究者回应.
     *
     * @return true 如果状态为RESPONDED
     */
    public boolean isResponded() {
        return status == QueryStatus.RESPONDED;
    }

    /**
     * 判断质疑是否已关闭.
     *
     * @return true 如果状态为CLOSED
     */
    public boolean isClosed() {
        return status == QueryStatus.CLOSED;
    }

    /**
     * 判断质疑是否监查质疑.
     *
     * @return true 如果是监查质疑
     */
    public boolean isMonitorQuery() {
        return type == QueryType.MONITOR_QUERY;
    }

    /**
     * 判断质疑是否稽查质疑.
     *
     * @return true 如果是稽查质疑
     */
    public boolean isAuditQuery() {
        return type == QueryType.AUDIT_QUERY;
    }

    // ========== Getter方法 ==========

    @Override
    public QueryId getId() {
        return id;
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

    /**
     * 获取质疑状态.
     *
     * @return QueryStatus
     */
    public QueryStatus getStatus() {
        return status;
    }

    /**
     * 获取质疑类型.
     *
     * @return QueryType
     */
    public QueryType getType() {
        return type;
    }

    /**
     * 获取质疑内容.
     *
     * @return 质疑内容描述
     */
    public String getQuestion() {
        return question;
    }

    /**
     * 获取研究者回应内容.
     *
     * @return 回应内容，可能为null
     */
    public String getResponse() {
        return response;
    }

    /**
     * 获取更新类型.
     *
     * @return QueryUpdateType，可能为null
     */
    public QueryUpdateType getUpdateType() {
        return updateType;
    }

    /**
     * 获取创建用户ID.
     *
     * @return 用户ID
     */
    public Long getCreateUserId() {
        return createUserId;
    }

    /**
     * 获取最后更新用户ID.
     *
     * @return 用户ID
     */
    public Long getUpdateUserId() {
        return updateUserId;
    }

    /**
     * 获取创建时间.
     *
     * @return Date
     */
    public Date getCreateTime() {
        return createTime != null ? (Date) createTime.clone() : null;
    }

    /**
     * 获取最后更新时间.
     *
     * @return Date
     */
    public Date getUpdateTime() {
        return updateTime != null ? (Date) updateTime.clone() : null;
    }

    /**
     * 获取质疑时的原值快照.
     *
     * @return SnapshotValue
     */
    public SnapshotValue getOriginalValue() {
        return originalValue;
    }

    /**
     * 获取当前值快照（可能已被修改）.
     *
     * @return SnapshotValue
     */
    public SnapshotValue getCurrentValue() {
        return currentValue;
    }

    @Override
    public String toString() {
        return "Query{"
                + "id=" + id
                + ", assessmentId=" + assessmentId
                + ", fieldIdentifier=" + fieldIdentifier
                + ", status=" + status
                + ", type=" + type
                + '}';
    }
}
