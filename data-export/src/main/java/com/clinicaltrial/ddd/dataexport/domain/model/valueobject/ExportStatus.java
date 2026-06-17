package com.clinicaltrial.ddd.dataexport.domain.model.valueobject;

/**
 * ExportStatus — 导出任务状态枚举.
 * <p>
 * 定义导出任务的生命周期状态：
 * <ul>
 *   <li>DRAFT(0) — 草稿，任务创建但未提交审批</li>
 *   <li>PENDING_APPROVAL(1) — 待审批，任务已提交等待审核</li>
 *   <li>APPROVED(2) — 已批准，审批通过准备执行导出</li>
 *   <li>EXPORTING(3) — 导出中，系统正在执行数据导出</li>
 *   <li>COMPLETED(4) — 已完成，导出成功</li>
 *   <li>FAILED(5) — 失败，导出过程发生错误</li>
 * </ul>
 * </p>
 *
 * <p>
 * 状态机转换规则：
 * <pre>
 * DRAFT ──→ PENDING_APPROVAL: 提交审批
 * PENDING_APPROVAL ──→ APPROVED: 审批通过
 * PENDING_APPROVAL ──→ DRAFT: 审批驳回
 * APPROVED ──→ EXPORTING: 开始导出
 * EXPORTING ──→ COMPLETED: 导出完成
 * EXPORTING ──→ FAILED: 导出失败
 * FAILED ──→ EXPORTING: 重试导出
 * </pre>
 * </p>
 */
public enum ExportStatus {

    /**
     * 草稿（0） — 任务创建但未提交审批.
     */
    DRAFT(0, "草稿"),

    /**
     * 待审批（1） — 任务已提交等待审核.
     */
    PENDING_APPROVAL(1, "待审批"),

    /**
     * 已批准（2） — 审批通过准备执行导出.
     */
    APPROVED(2, "已批准"),

    /**
     * 导出中（3） — 系统正在执行数据导出.
     */
    EXPORTING(3, "导出中"),

    /**
     * 已完成（4） — 导出成功.
     */
    COMPLETED(4, "已完成"),

    /**
     * 失败（5） — 导出过程发生错误.
     */
    FAILED(5, "失败");

    private final int code;
    private final String description;

    /**
     * 构造ExportStatus.
     *
     * @param code        状态编码
     * @param description 状态描述
     */
    ExportStatus(int code, String description) {
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
    public static ExportStatus fromCode(int code) {
        for (ExportStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ExportStatus code: " + code);
    }

    @Override
    public String toString() {
        return code + "(" + description + ")";
    }
}
