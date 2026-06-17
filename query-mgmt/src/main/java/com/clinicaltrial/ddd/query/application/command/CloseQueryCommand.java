package com.clinicaltrial.ddd.query.application.command;

import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;

import java.util.Objects;

/**
 * CloseQueryCommand — 关闭质疑指令.
 * <p>
 * 封装关闭质疑所需的输入参数。
 * 由监查员在审核研究者回应后执行，将质疑状态从OPEN或RESPONDED转为CLOSED。
 * 由应用层服务处理，转换为领域操作。
 * </p>
 */
public class CloseQueryCommand {

    private final QueryId queryId;
    private final Long userId;

    /**
     * 构造CloseQueryCommand.
     *
     * @param queryId 质疑ID
     * @param userId  关闭操作用户ID
     */
    public CloseQueryCommand(QueryId queryId, Long userId) {
        this.queryId = Objects.requireNonNull(queryId, "queryId must not be null");
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
     * 获取操作用户ID.
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return userId;
    }
}
