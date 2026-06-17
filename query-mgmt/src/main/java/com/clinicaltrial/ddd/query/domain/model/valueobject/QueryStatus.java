package com.clinicaltrial.ddd.query.domain.model.valueobject;

/**
 * QueryStatus — 质疑状态枚举.
 * <p>
 * 定义质疑记录的生命周期状态：
 * <ul>
 *   <li>OPEN(0) — 质疑已提出，待研究者回应</li>
 *   <li>RESPONDED(1) — 研究者已回应，待监查员关闭</li>
 *   <li>CLOSED(2) — 质疑已关闭，质疑流程结束</li>
 * </ul>
 * </p>
 *
 * <p>
 * 状态机转换规则：
 * <pre>
 * OPEN ──→ RESPONDED: 研究者回应质疑
 * OPEN ──→ CLOSED:   监查员直接关闭质疑
 * RESPONDED ──→ CLOSED: 监查员接受回应并关闭质疑
 * CLOSED ──→ OPEN:     重新打开已关闭的质疑（需新的理由）
 * </pre>
 * </p>
 */
public enum QueryStatus {

    /**
     * 待回应（0） — 质疑已提出，等待研究者回应.
     */
    OPEN(0, "待回应"),

    /**
     * 已回应（1） — 研究者已提交回复.
     */
    RESPONDED(1, "已回应"),

    /**
     * 已关闭（2） — 质疑已关闭，流程结束.
     */
    CLOSED(2, "已关闭");

    private final int code;
    private final String description;

    /**
     * 构造QueryStatus.
     *
     * @param code        状态编码
     * @param description 状态描述
     */
    QueryStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取状态编码.
     *
     * @return 整数编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取状态描述.
     *
     * @return 中文描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据编码获取枚举值.
     *
     * @param code 状态编码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果编码无效
     */
    public static QueryStatus fromCode(int code) {
        for (QueryStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown QueryStatus code: " + code);
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
