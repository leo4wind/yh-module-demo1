package com.clinicaltrial.ddd.query.application.command;

import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import java.util.Objects;

/**
 * ReopenQueryCommand — 重新打开质疑指令.
 * <p>
 * 封装重新打开已关闭质疑所需的输入参数。
 * 当监查员对之前的处理结果不满意时，可以重新打开已关闭的质疑并附上新的理由。
 * 由应用层服务处理，转换为领域操作。
 * </p>
 */
public class ReopenQueryCommand {

    private final QueryId queryId;
    private final String reason;
    private final Long userId;

    /**
     * 构造ReopenQueryCommand.
     *
     * @param queryId 质疑ID
     * @param reason  重新打开的原因说明
     * @param userId  操作用户ID
     */
    public ReopenQueryCommand(QueryId queryId, String reason, Long userId) {
        this.queryId = Objects.requireNonNull(queryId, "queryId must not be null");
        this.reason = Objects.requireNonNull(reason, "reason must not be null");
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
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
     * 获取重新打开的原因.
     *
     * @return 原因说明
     */
    public String getReason() {
        return reason;
    }

    /**
     * 获取操作用户ID.
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }
}
