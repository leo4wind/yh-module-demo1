package com.clinicaltrial.ddd.datacollection.domain.model.valueobject;

/**
 * SubjectStageStatus — 受试者阶段状态枚举.
 * <p>
 * 定义SubjectStage（受试者阶段）的生命周期状态：
 * <ul>
 *   <li>PENDING — 待开始，初始状态</li>
 *   <li>IN_PROGRESS — 进行中，已开始数据采集</li>
 *   <li>COMPLETED — 已完成，所有CRF评估完成</li>
 * </ul>
 * 状态机：PENDING → IN_PROGRESS → COMPLETED
 * </p>
 */
public enum SubjectStageStatus {

    /**
     * 待开始 — 初始状态.
     * 阶段已生成但尚未开始数据采集。
     */
    PENDING,

    /**
     * 进行中 — 已开始数据采集.
     * 至少一个CRF评估已开始填写。
     */
    IN_PROGRESS,

    /**
     * 已完成 — 所有CRF评估完成.
     * 阶段内所有CRF评估均已完成，不可再修改。
     */
    COMPLETED
}
