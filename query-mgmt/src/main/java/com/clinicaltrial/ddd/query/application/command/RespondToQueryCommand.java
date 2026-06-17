package com.clinicaltrial.ddd.query.application.command;

import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryId;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryUpdateType;

import java.util.Objects;

/**
 * RespondToQueryCommand — 回应质疑指令.
 * <p>
 * 封装研究者回应质疑所需的全部输入参数。
 * 包括回应内容、更新类型（修改值/仅澄清），以及如果修改了数据则包含新值信息。
 * 由应用层服务处理，转换为领域操作。
 * </p>
 */
public class RespondToQueryCommand {

    private final QueryId queryId;
    private final String response;
    private final QueryUpdateType updateType;
    private final String newFieldValue;
    private final String newFieldValueText;
    private final Long userId;

    /**
     * 构造RespondToQueryCommand.
     *
     * @param queryId         质疑ID
     * @param response        研究者回应内容
     * @param updateType      更新类型（MODIFY_VALUE/CLARIFY_ONLY）
     * @param newFieldValue   新字段值（编码值），仅MODIFY_VALUE时需要
     * @param newFieldValueText 新字段值显示文本，仅MODIFY_VALUE时需要
     * @param userId          操作用户ID
     */
    public RespondToQueryCommand(QueryId queryId,
                                  String response,
                                  QueryUpdateType updateType,
                                  String newFieldValue,
                                  String newFieldValueText,
                                  Long userId) {
        this.queryId = Objects.requireNonNull(queryId, "queryId must not be null");
        this.response = Objects.requireNonNull(response, "response must not be null");
        this.updateType = Objects.requireNonNull(updateType, "updateType must not be null");
        this.newFieldValue = newFieldValue;
        this.newFieldValueText = newFieldValueText;
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
     * 获取研究者回应内容.
     *
     * @return 回应内容
     */
    public String getResponse() {
        return response;
    }

    /**
     * 获取更新类型.
     *
     * @return QueryUpdateType
     */
    public QueryUpdateType getUpdateType() {
        return updateType;
    }

    /**
     * 获取新字段值（编码值）.
     *
     * @return 字段值，可能为null
     */
    public String getNewFieldValue() {
        return newFieldValue;
    }

    /**
     * 获取新字段值显示文本.
     *
     * @return 显示文本，可能为null
     */
    public String getNewFieldValueText() {
        return newFieldValueText;
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
