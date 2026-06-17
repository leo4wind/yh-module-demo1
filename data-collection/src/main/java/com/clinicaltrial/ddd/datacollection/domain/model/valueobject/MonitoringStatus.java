package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

/**
 * MonitoringStatus — CRF评估监控状态枚举.
 * <p>
 * 定义CrfAssessment（CRF评估记录）的监控生命周期状态：
 * <ul>
 *   <li>PENDING(0) — 待填写，初始状态</li>
 *   <li>IN_PROGRESS(1) — 填写中，已有部分字段被填写</li>
 *   <li>COMPLETED(2) — 已完成，所有必填字段已填写</li>
 *   <li>QUERIED(3) — 被质疑，收到查询后在已完成状态下触发</li>
 *   <li>AUDITED(4) — 已审核，监查员审核通过</li>
 * </ul>
 * 关键状态机规则：
 * <pre>
 * 0(待填写) → 1(填写中): 首次保存字段值
 * 0/1 → 2(已完成): 完整性达到100%
 * 2 → 3(被质疑): 收到QueryRaisedEvent（外部触发）
 * 3 → 2(已完成): 所有质疑关闭
 * 2/3 → 4(已审核): 执行审核操作
 * </pre>
 * 自动转换（完整性驱动）仅在当前状态为0或1时有效。
 * 状态2及以上时，状态变更由事件驱动。
 * </p>
 */
public enum MonitoringStatus {

    /**
     * 待填写（0） — 初始状态.
     * 尚未填写任何字段值。
     */
    PENDING(0, "待填写"),

    /**
     * 填写中（1） — 已有部分字段被填写.
     * 完整性大于0%但小于100%。
     */
    IN_PROGRESS(1, "填写中"),

    /**
     * 已完成（2） — 所有必填字段已填写.
     * 完整性达到100%。
     */
    COMPLETED(2, "已完成"),

    /**
     * 被质疑（3） — 数据被质疑.
     * 由外部查询事件触发，从已完成状态转入。
     */
    QUERIED(3, "被质疑"),

    /**
     * 已审核（4） — 监查员审核通过.
     * 已完成或被质疑状态下均可执行审核。
     */
    AUDITED(4, "已审核");

    private final int code;
    private final String description;

    /**
     * 构造MonitoringStatus.
     *
     * @param code        状态编码
     * @param description 状态描述
     */
    MonitoringStatus(int code, String description) {
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
     * 判断当前状态是否允许编辑字段值.
     * <p>
     * 仅 PENDING(0) 和 IN_PROGRESS(1) 状态下允许编辑。
     * </p>
     *
     * @return true 如果允许编辑
     */
    public boolean canEdit() {
        return this == PENDING || this == IN_PROGRESS;
    }

    /**
     * 判断当前状态是否允许自动转换（基于完整性）.
     * <p>
     * 仅 PENDING(0) 和 IN_PROGRESS(1) 状态下允许完整性驱动的自动转换。
     * </p>
     *
     * @return true 如果允许自动转换
     */
    public boolean canAutoTransition() {
        return this == PENDING || this == IN_PROGRESS;
    }

    /**
     * 判断当前状态是否允许审核.
     *
     * @return true 如果允许审核
     */
    public boolean canAudit() {
        return this == COMPLETED || this == QUERIED;
    }

    /**
     * 根据编码获取枚举值.
     *
     * @param code 状态编码
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果编码无效
     */
    public static MonitoringStatus fromCode(int code) {
        for (MonitoringStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown MonitoringStatus code: " + code);
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
