package com.clinicaltrial.ddd.query.domain.model.valueobject;

/**
 * QueryType — 质疑类型枚举.
 * <p>
 * 定义质疑的来源类型：
 * <ul>
 *   <li>MONITOR_QUERY — 监查质疑：由临床监查员（CRA）在数据核查过程中提出</li>
 *   <li>AUDIT_QUERY — 稽查质疑：由稽查员在稽查过程中提出</li>
 * </ul>
 * </p>
 */
public enum QueryType {

    /**
     * 监查质疑 — 由临床监查员（CRA）提出的数据质疑.
     */
    MONITOR_QUERY("MONITOR_QUERY", "监查质疑"),

    /**
     * 稽查质疑 — 由稽查员提出的数据质疑.
     */
    AUDIT_QUERY("AUDIT_QUERY", "稽查质疑");

    private final String code;
    private final String description;

    /**
     * 构造QueryType.
     *
     * @param code        类型编码
     * @param description 类型描述
     */
    QueryType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取类型编码.
     *
     * @return 编码字符串
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取类型描述.
     *
     * @return 中文描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据编码获取枚举值.
     *
     * @param code 类型编码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果编码无效
     */
    public static QueryType fromCode(String code) {
        for (QueryType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown QueryType code: " + code);
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
