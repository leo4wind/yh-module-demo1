package com.clinicaltrial.ddd.statistics.domain.model.valueobject;

/**
 * ResultStatus — 分析结果状态枚举.
 * <p>
 * 定义分析配置的执行状态：
 * <ul>
 *   <li>PENDING — 待执行，分析已配置但未运行</li>
 *   <li>RUNNING — 执行中，分析算法正在运行</li>
 *   <li>COMPLETED — 已完成，分析成功完成</li>
 *   <li>FAILED — 失败，分析过程发生错误</li>
 * </ul>
 * </p>
 */
public enum ResultStatus {

    /**
     * 待执行（0） — 分析已配置，等待运行.
     */
    PENDING(0, "待执行"),

    /**
     * 执行中（1） — 分析算法正在运行.
     */
    RUNNING(1, "执行中"),

    /**
     * 已完成（2） — 分析成功完成.
     */
    COMPLETED(2, "已完成"),

    /**
     * 失败（3） — 分析过程发生错误.
     */
    FAILED(3, "失败");

    private final int code;
    private final String description;

    /**
     * 构造ResultStatus.
     *
     * @param code        编码
     * @param description 描述
     */
    ResultStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取编码.
     *
     * @return 整数编码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取描述.
     *
     * @return 中文描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据编码获取枚举值.
     *
     * @param code 编码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果编码无效
     */
    public static ResultStatus fromCode(int code) {
        for (ResultStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ResultStatus code: " + code);
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
