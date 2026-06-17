package com.clinicaltrial.ddd.query.domain.model.valueobject;

/**
 * QueryUpdateType — 质疑处理更新类型枚举.
 * <p>
 * 定义研究者回应质疑时对数据执行的操作类型：
 * <ul>
 *   <li>MODIFY_VALUE — 修改值：研究者确认数据有误并修改了字段值</li>
 *   <li>CLARIFY_ONLY — 仅澄清：研究者仅对质疑内容作出说明，未修改数据值</li>
 * </ul>
 * </p>
 */
public enum QueryUpdateType {

    /**
     * 修改值 — 研究者修改了被质疑字段的值.
     */
    MODIFY_VALUE("MODIFY_VALUE", "修改值"),

    /**
     * 仅澄清 — 研究者仅作出文字说明，未修改字段值.
     */
    CLARIFY_ONLY("CLARIFY_ONLY", "仅澄清");

    private final String code;
    private final String description;

    /**
     * 构造QueryUpdateType.
     *
     * @param code        类型编码
     * @param description 类型描述
     */
    QueryUpdateType(String code, String description) {
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
    public static QueryUpdateType fromCode(String code) {
        for (QueryUpdateType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown QueryUpdateType code: " + code);
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
