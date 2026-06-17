package com.clinicaltrial.ddd.datacollection.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.datacollection.domain.event.StageCompletedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.StageEventGeneratedEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.BaselineTime;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentRef;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.FollowUpPeriod;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.StageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageStatus;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.VisitPlanId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * SubjectStage — 受试者阶段聚合根.
 * <p>
 * 表示某个受试者在某个试验阶段（如筛选期、治疗期、随访期）的数据采集活动。
 * 每个SubjectStage包含多个CRF评估引用（CrfAssessmentRef），
 * 当所有CRF评估都完成时，阶段本身也进入完成状态。
 * </p>
 *
 * <p>
 * 生命周期状态机：
 * <pre>
 * PENDING → IN_PROGRESS → COMPLETED
 * </pre>
 * </p>
 */
public class SubjectStage extends AggregateRoot<SubjectStageId> {

    private SubjectStageId id;
    private SubjectId subjectsUserId;
    private StageId stageId;
    private VisitPlanId planEventId;
    private SubjectStageStatus status;
    private Date stageStartAt;
    private Date stageEndAt;
    private BaselineTime baselineTime;
    private FollowUpPeriod followUpPeriod;
    private String followUpStatus;
    private Date completeTime;
    private Long completeUserId;
    private List<CrfAssessmentRef> crfAssessmentRefs;

    /**
     * 全参构造函数 — 供工厂方法和重建使用.
     */
    private SubjectStage() {
        this.crfAssessmentRefs = new ArrayList<>();
    }

    // ========== 工厂方法 ==========

    /**
     * 创建新的SubjectStage（受试者阶段生成）.
     * <p>
     * 当受试者入组后，系统为每个自动添加的阶段调用此方法创建SubjectStage。
     * 初始状态为PENDING，同时触发StageEventGeneratedEvent。
     * </p>
     *
     * @param id           受试者阶段ID
     * @param subjectsUserId 受试者ID
     * @param stageId      试验阶段ID
     * @param planEventId  触发的访视计划ID
     * @param baselineTime 基线时间
     * @return 新建的SubjectStage实例
     */
    public static SubjectStage create(SubjectStageId id, SubjectId subjectsUserId,
                                       StageId stageId, VisitPlanId planEventId,
                                       BaselineTime baselineTime) {
        SubjectStage stage = new SubjectStage();
        stage.id = id;
        stage.subjectsUserId = subjectsUserId;
        stage.stageId = stageId;
        stage.planEventId = planEventId;
        stage.status = SubjectStageStatus.PENDING;
        stage.baselineTime = baselineTime;
        stage.crfAssessmentRefs = new ArrayList<>();

        stage.registerEvent(new StageEventGeneratedEvent(id, subjectsUserId, stageId, planEventId));
        return stage;
    }

    /**
     * 从持久化存储重建SubjectStage（不含副作用）.
     *
     * @param id               受试者阶段ID
     * @param subjectsUserId   受试者ID
     * @param stageId          试验阶段ID
     * @param planEventId      访视计划ID
     * @param status           当前状态
     * @param stageStartAt     阶段开始时间
     * @param stageEndAt       阶段结束时间
     * @param baselineTime     基线时间
     * @param followUpPeriod   随访周期
     * @param followUpStatus   随访状态
     * @param completeTime     完成时间
     * @param completeUserId   完成操作用户ID
     * @param crfAssessmentRefs CRF评估引用列表
     * @return 重建的SubjectStage实例
     */
    public static SubjectStage reconstruct(SubjectStageId id, SubjectId subjectsUserId,
                                            StageId stageId, VisitPlanId planEventId,
                                            SubjectStageStatus status,
                                            Date stageStartAt, Date stageEndAt,
                                            BaselineTime baselineTime,
                                            FollowUpPeriod followUpPeriod,
                                            String followUpStatus,
                                            Date completeTime, Long completeUserId,
                                            List<CrfAssessmentRef> crfAssessmentRefs) {
        SubjectStage stage = new SubjectStage();
        stage.id = id;
        stage.subjectsUserId = subjectsUserId;
        stage.stageId = stageId;
        stage.planEventId = planEventId;
        stage.status = status;
        stage.stageStartAt = stageStartAt != null ? (Date) stageStartAt.clone() : null;
        stage.stageEndAt = stageEndAt != null ? (Date) stageEndAt.clone() : null;
        stage.baselineTime = baselineTime;
        stage.followUpPeriod = followUpPeriod;
        stage.followUpStatus = followUpStatus;
        stage.completeTime = completeTime != null ? (Date) completeTime.clone() : null;
        stage.completeUserId = completeUserId;
        stage.crfAssessmentRefs = crfAssessmentRefs != null
                ? new ArrayList<>(crfAssessmentRefs)
                : new ArrayList<>();
        return stage;
    }

    // ========== 业务方法 ==========

    /**
     * 开始阶段数据采集.
     * <p>
     * 状态转换：PENDING → IN_PROGRESS
     * 记录阶段开始时间。如果已有CRF评估引用，则创建初始评估（由调用方处理）。
     * </p>
     *
     * @throws BusinessRuleViolationException 如果当前状态不是PENDING
     */
    public void start() {
        if (status != SubjectStageStatus.PENDING) {
            throw new BusinessRuleViolationException(
                    "SubjectStage can only be started when PENDING, current: " + status);
        }
        this.status = SubjectStageStatus.IN_PROGRESS;
        this.stageStartAt = new Date();
    }

    /**
     * 完成阶段数据采集.
     * <p>
     * 状态转换：IN_PROGRESS → COMPLETED
     * 验证所有引用的CRF评估是否已完成。
     * 设置完成时间和完成用户。
     * 触发StageCompletedEvent。
     * </p>
     *
     * @param completedAssessmentIds 当前已完成的CRF评估ID集合（由调用方从外部加载提供）
     * @param completeUserId         完成操作用户ID
     * @throws BusinessRuleViolationException 如果状态不是IN_PROGRESS，或仍有未完成的CRF评估
     */
    public void complete(Set<CrfAssessmentId> completedAssessmentIds, Long completeUserId) {
        if (status != SubjectStageStatus.IN_PROGRESS) {
            throw new BusinessRuleViolationException(
                    "SubjectStage can only be completed when IN_PROGRESS, current: " + status);
        }
        if (!isReadyToComplete(completedAssessmentIds)) {
            throw new BusinessRuleViolationException(
                    "SubjectStage cannot be completed: not all CRF assessments are completed");
        }
        this.status = SubjectStageStatus.COMPLETED;
        this.completeTime = new Date();
        this.completeUserId = completeUserId;

        registerEvent(new StageCompletedEvent(id, subjectsUserId, stageId));
    }

    /**
     * 添加CRF评估引用.
     * <p>
     * 将CrfAssessment的ID添加到本阶段的引用列表中。
     * 仅允许在PENDING或IN_PROGRESS状态下添加。
     * </p>
     *
     * @param assessmentId CRF评估ID
     * @throws BusinessRuleViolationException 如果阶段已完成或状态不允许
     */
    public void addCrfAssessmentRef(CrfAssessmentId assessmentId) {
        if (status == SubjectStageStatus.COMPLETED) {
            throw new BusinessRuleViolationException(
                    "Cannot add CRF assessment ref to a completed SubjectStage");
        }
        CrfAssessmentRef ref = new CrfAssessmentRef(assessmentId);
        if (!crfAssessmentRefs.contains(ref)) {
            crfAssessmentRefs.add(ref);
        }
    }

    /**
     * 判断是否所有引用的CRF评估都已完成.
     *
     * @param completedAssessmentIds 当前已完成的CRF评估ID集合
     * @return true 如果所有引用的CRF评估都已完成或无引用
     */
    public boolean isReadyToComplete(Set<CrfAssessmentId> completedAssessmentIds) {
        if (crfAssessmentRefs == null || crfAssessmentRefs.isEmpty()) {
            return true;
        }
        return crfAssessmentRefs.stream()
                .map(CrfAssessmentRef::getAssessmentId)
                .allMatch(completedAssessmentIds::contains);
    }

    // ========== getter方法（仅查询） ==========

    @Override
    public SubjectStageId getId() {
        return id;
    }

    /**
     * 获取受试者ID.
     *
     * @return SubjectId
     */
    public SubjectId getSubjectsUserId() {
        return subjectsUserId;
    }

    /**
     * 获取试验阶段ID.
     *
     * @return StageId
     */
    public StageId getStageId() {
        return stageId;
    }

    /**
     * 获取触发的访视计划ID.
     *
     * @return VisitPlanId
     */
    public VisitPlanId getPlanEventId() {
        return planEventId;
    }

    /**
     * 获取当前状态.
     *
     * @return SubjectStageStatus
     */
    public SubjectStageStatus getStatus() {
        return status;
    }

    /**
     * 获取阶段开始时间.
     *
     * @return 开始时间
     */
    public Date getStageStartAt() {
        return stageStartAt != null ? (Date) stageStartAt.clone() : null;
    }

    /**
     * 获取阶段结束时间.
     *
     * @return 结束时间
     */
    public Date getStageEndAt() {
        return stageEndAt != null ? (Date) stageEndAt.clone() : null;
    }

    /**
     * 获取基线时间.
     *
     * @return BaselineTime
     */
    public BaselineTime getBaselineTime() {
        return baselineTime;
    }

    /**
     * 获取随访周期.
     *
     * @return FollowUpPeriod
     */
    public FollowUpPeriod getFollowUpPeriod() {
        return followUpPeriod;
    }

    /**
     * 获取随访状态.
     *
     * @return 随访状态字符串
     */
    public String getFollowUpStatus() {
        return followUpStatus;
    }

    /**
     * 获取完成时间.
     *
     * @return 完成时间
     */
    public Date getCompleteTime() {
        return completeTime != null ? (Date) completeTime.clone() : null;
    }

    /**
     * 获取完成操作用户ID.
     *
     * @return 用户ID
     */
    public Long getCompleteUserId() {
        return completeUserId;
    }

    /**
     * 获取CRF评估引用列表（不可修改视图）.
     *
     * @return 不可修改的CrfAssessmentRef列表
     */
    public List<CrfAssessmentRef> getCrfAssessmentRefs() {
        return crfAssessmentRefs != null
                ? Collections.unmodifiableList(crfAssessmentRefs)
                : Collections.emptyList();
    }

    // ========== setter供重建用 ==========

    public void setFollowUpPeriod(FollowUpPeriod followUpPeriod) {
        this.followUpPeriod = followUpPeriod;
    }

    public void setFollowUpStatus(String followUpStatus) {
        this.followUpStatus = followUpStatus;
    }

    @Override
    public String toString() {
        return "SubjectStage{"
                + "id=" + id
                + ", subjectsUserId=" + subjectsUserId
                + ", stageId=" + stageId
                + ", status=" + status
                + '}';
    }
}
