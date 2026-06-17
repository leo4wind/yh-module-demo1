package com.clinicaltrial.ddd.datacollection.domain.model.aggregate;

import com.clinicaltrial.ddd.common.model.AggregateRoot;
import com.clinicaltrial.ddd.common.model.BusinessRuleViolationException;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfAuditedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfCompletedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfCompletenessChangedEvent;
import com.clinicaltrial.ddd.datacollection.domain.event.CrfFieldValueChangedEvent;
import com.clinicaltrial.ddd.datacollection.domain.model.CrfField;
import com.clinicaltrial.ddd.datacollection.domain.model.entity.CrfFieldValue;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.AssessmentScore;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfTemplateId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfVersionId;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.MonitoringStatus;
import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.SubjectStageId;
import com.clinicaltrial.ddd.subject.domain.model.valueobject.SubjectId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * CrfAssessment — CRF评估记录聚合根.
 * <p>
 * 表示受试者在某个阶段对某个CRF模板的评估记录，包含所有已填写的字段值。
 * 每个CrfAssessment属于一个SubjectStage，通过subjectsStageId关联。
 * </p>
 *
 * <p>
 * 监控状态机（MonitoringStatus）：
 * <pre>
 * PENDING(0) ──→ IN_PROGRESS(1) ──→ COMPLETED(2) ──→ QUERIED(3) ──→ AUDITED(4)
 *                     ↑                  ↑ ↓              ↓
 *                     └──────────────────┘ └── 所有质疑关闭 ──┘
 * </pre>
 * 自动转换（完整性驱动）仅在状态为PENDING或IN_PROGRESS时有效。
 * 一旦状态达到COMPLETED及以上，状态变更由事件驱动（质疑、审核）。
 * </p>
 */
public class CrfAssessment extends AggregateRoot<CrfAssessmentId> {

    private CrfAssessmentId id;
    private SubjectId subjectsUserId;
    private CrfTemplateId crfId;
    private CrfVersionId crfVersionId;
    private SubjectStageId subjectsStageId;
    private MonitoringStatus status;
    private Completeness completeness;
    private boolean adverseEvent;
    private AssessmentScore assessmentScore;
    private List<CrfFieldValue> fieldValues;

    /**
     * 私有构造函数，通过工厂方法创建.
     */
    private CrfAssessment() {
        this.fieldValues = new ArrayList<>();
    }

    // ========== 工厂方法 ==========

    /**
     * 创建新的CrfAssessment（CRF评估记录）.
     * <p>
     * 初始状态：PENDING(0)，完整性：0%。
     * </p>
     *
     * @param id              CRF评估ID
     * @param subjectsUserId  受试者ID
     * @param crfId           CRF模板ID
     * @param crfVersionId    CRF版本ID
     * @param subjectsStageId 所属受试者阶段ID
     * @return 新建的CrfAssessment实例
     */
    public static CrfAssessment create(CrfAssessmentId id, SubjectId subjectsUserId,
                                        CrfTemplateId crfId, CrfVersionId crfVersionId,
                                        SubjectStageId subjectsStageId) {
        CrfAssessment assessment = new CrfAssessment();
        assessment.id = id;
        assessment.subjectsUserId = subjectsUserId;
        assessment.crfId = crfId;
        assessment.crfVersionId = crfVersionId;
        assessment.subjectsStageId = subjectsStageId;
        assessment.status = MonitoringStatus.PENDING;
        assessment.completeness = Completeness.zero();
        assessment.adverseEvent = false;
        assessment.fieldValues = new ArrayList<>();
        return assessment;
    }

    /**
     * 从持久化存储重建CrfAssessment（不含副作用）.
     *
     * @param id              CRF评估ID
     * @param subjectsUserId  受试者ID
     * @param crfId           CRF模板ID
     * @param crfVersionId    CRF版本ID
     * @param subjectsStageId 所属受试者阶段ID
     * @param status          监控状态
     * @param completeness    完整性
     * @param adverseEvent    是否不良事件
     * @param assessmentScore 评估分数
     * @param fieldValues     字段值列表
     * @return 重建的CrfAssessment实例
     */
    public static CrfAssessment reconstruct(CrfAssessmentId id, SubjectId subjectsUserId,
                                              CrfTemplateId crfId, CrfVersionId crfVersionId,
                                              SubjectStageId subjectsStageId,
                                              MonitoringStatus status,
                                              Completeness completeness,
                                              boolean adverseEvent,
                                              AssessmentScore assessmentScore,
                                              List<CrfFieldValue> fieldValues) {
        CrfAssessment assessment = new CrfAssessment();
        assessment.id = id;
        assessment.subjectsUserId = subjectsUserId;
        assessment.crfId = crfId;
        assessment.crfVersionId = crfVersionId;
        assessment.subjectsStageId = subjectsStageId;
        assessment.status = status;
        assessment.completeness = completeness;
        assessment.adverseEvent = adverseEvent;
        assessment.assessmentScore = assessmentScore;
        assessment.fieldValues = fieldValues != null ? new ArrayList<>(fieldValues) : new ArrayList<>();
        return assessment;
    }

    // ========== 业务方法 ==========

    /**
     * 保存字段值.
     * <p>
     * 只有在PENDING(0)或IN_PROGRESS(1)状态下允许编辑。
     * 如果字段编码已存在则更新值，否则新增。
     * 触发CrfFieldValueChangedEvent。
     * </p>
     *
     * @param value   字段值实体（不含ID时为新增，含ID且fieldCode已存在时为更新）
     * @param userId  操作用户ID
     * @throws BusinessRuleViolationException 如果当前状态不允许编辑
     */
    public void saveFieldValue(CrfFieldValue value, Long userId) {
        if (!status.canEdit()) {
            throw new BusinessRuleViolationException(
                    "Cannot save field value when status is " + status
                    + ". Only PENDING(0) or IN_PROGRESS(1) allowed.");
        }

        // 查找是否已存在同编码字段
        Optional<CrfFieldValue> existing = findFieldValueByCode(value.getFieldCode());

        String oldValue = null;
        if (existing.isPresent()) {
            // 更新已有字段值
            CrfFieldValue existingValue = existing.get();
            oldValue = existingValue.getFieldValue();
            existingValue.updateValue(value.getFieldValue(), value.getFieldValueText());
        } else {
            // 新增字段值
            fieldValues.add(value);
        }

        // 注册字段值变更事件
        registerEvent(new CrfFieldValueChangedEvent(
                id, value.getFieldCode(), oldValue, value.getFieldValue(), userId));
    }

    /**
     * 计算完整性并自动执行状态转换.
     * <p>
     * 根据模板字段定义统计已填写的必填字段数，计算完成百分比。
     * 自动状态转换（仅在当前状态为PENDING或IN_PROGRESS时）：
     * <ul>
     *   <li>percentage >= 100 → COMPLETED(2)</li>
     *   <li>percentage > 0 → IN_PROGRESS(1)</li>
     *   <li>percentage = 0 → PENDING(0)</li>
     * </ul>
     * 触发CrfCompletenessChangedEvent，如果状态变为COMPLETED则触发CrfCompletedEvent。
     * </p>
     *
     * @param templateFields CRF模板字段定义列表（用于计算完整性）
     * @throws BusinessRuleViolationException 如果当前状态不允许自动转换（状态>=2）
     */
    public void calculateCompleteness(List<CrfField> templateFields) {
        if (!status.canAutoTransition()) {
            throw new BusinessRuleViolationException(
                    "Cannot auto-transition when status is " + status
                    + ". Only PENDING(0) or IN_PROGRESS(1) allowed.");
        }

        // 统计应参与计算的字段（必填且非隐藏）
        List<CrfField> countableFields = templateFields.stream()
                .filter(CrfField::isCountable)
                .collect(Collectors.toList());

        int totalCount = countableFields.size();
        int filledCount = 0;

        for (CrfField field : countableFields) {
            boolean filled = fieldValues.stream()
                    .anyMatch(fv -> fv.matches(field.getFieldCode())
                            && fv.getFieldValue() != null
                            && !fv.getFieldValue().trim().isEmpty());
            if (filled) {
                filledCount++;
            }
        }

        // 计算百分比
        BigDecimal percentage;
        if (totalCount == 0) {
            percentage = BigDecimal.ZERO;
        } else {
            percentage = BigDecimal.valueOf(filledCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCount), 2, BigDecimal.ROUND_HALF_UP);
        }

        Completeness oldCompleteness = this.completeness;
        Completeness newCompleteness = new Completeness(percentage, filledCount, totalCount);

        // 如果完整性未变化则不触发事件
        if (oldCompleteness.equals(newCompleteness)) {
            return;
        }

        this.completeness = newCompleteness;
        registerEvent(new CrfCompletenessChangedEvent(id, oldCompleteness, newCompleteness));

        // 自动状态转换
        if (newCompleteness.isComplete() && status != MonitoringStatus.COMPLETED) {
            this.status = MonitoringStatus.COMPLETED;
            registerEvent(new CrfCompletedEvent(id, subjectsStageId));
        } else if (percentage.compareTo(BigDecimal.ZERO) > 0
                && status == MonitoringStatus.PENDING) {
            this.status = MonitoringStatus.IN_PROGRESS;
        }
    }

    /**
     * 自动评分.
     * <p>
     * 当CRF评估完成后，根据评分规则对字段值进行评分。
     * 必须在COMPLETED或更高状态下执行。
     * </p>
     *
     * @param scoringRules 评分规则（字段编码 → 权重/分值）
     * @throws BusinessRuleViolationException 如果状态低于COMPLETED
     */
    public void autoScore(Map<String, BigDecimal> scoringRules) {
        if (status.getCode() < MonitoringStatus.COMPLETED.getCode()) {
            throw new BusinessRuleViolationException(
                    "Cannot auto-score when status is " + status
                    + ". Assessment must be at least COMPLETED(2).");
        }

        if (scoringRules == null || scoringRules.isEmpty()) {
            this.assessmentScore = new AssessmentScore("0", "no-rules");
            return;
        }

        BigDecimal totalScore = BigDecimal.ZERO;
        int matchedRules = 0;

        for (CrfFieldValue fieldValue : fieldValues) {
            BigDecimal weight = scoringRules.get(fieldValue.getFieldCode());
            if (weight != null) {
                // 如果字段有值，按权重加分
                if (fieldValue.getFieldValue() != null && !fieldValue.getFieldValue().trim().isEmpty()) {
                    totalScore = totalScore.add(weight);
                }
                matchedRules++;
            }
        }

        String result;
        if (matchedRules == 0) {
            result = "no-matching-rules";
        } else {
            result = totalScore.compareTo(BigDecimal.ZERO) > 0 ? "normal" : "abnormal";
        }

        this.assessmentScore = new AssessmentScore(totalScore.toString(), result);
    }

    /**
     * 提出质疑（状态转换：COMPLETED(2) → QUERIED(3)）.
     * <p>
     * 当数据质疑被提出时，评估从已完成状态转为被质疑状态。
     * </p>
     *
     * @throws BusinessRuleViolationException 如果当前状态不是COMPLETED
     */
    public void raiseQuery() {
        if (status != MonitoringStatus.COMPLETED) {
            throw new BusinessRuleViolationException(
                    "Cannot raise query: assessment must be COMPLETED(2), current: " + status);
        }
        this.status = MonitoringStatus.QUERIED;
    }

    /**
     * 解决所有质疑（状态转换：QUERIED(3) → COMPLETED(2)）.
     * <p>
     * 当所有针对此评估的质疑都被关闭时，状态恢复为已完成。
     * </p>
     *
     * @throws BusinessRuleViolationException 如果当前状态不是QUERIED
     */
    public void resolveAllQueries() {
        if (status != MonitoringStatus.QUERIED) {
            throw new BusinessRuleViolationException(
                    "Cannot resolve queries: assessment must be QUERIED(3), current: " + status);
        }
        this.status = MonitoringStatus.COMPLETED;
    }

    /**
     * 审核通过（状态转换：COMPLETED(2) → AUDITED(4) 或 QUERIED(3) → AUDITED(4)）.
     * <p>
     * 监查员审核CRF评估数据，审核通过后状态变为已审核。
     * 触发CrfAuditedEvent。
     * </p>
     *
     * @param auditUserId 执行审核的用户ID
     * @throws BusinessRuleViolationException 如果当前状态不允许审核
     */
    public void audit(Long auditUserId) {
        if (!status.canAudit()) {
            throw new BusinessRuleViolationException(
                    "Cannot audit: assessment must be COMPLETED(2) or QUERIED(3), current: " + status);
        }
        this.status = MonitoringStatus.AUDITED;
        registerEvent(new CrfAuditedEvent(id, auditUserId));
    }

    // ========== 查询方法 ==========

    /**
     * 按字段编码查找字段值.
     *
     * @param fieldCode 字段编码
     * @return 包含CrfFieldValue的Optional
     */
    public Optional<CrfFieldValue> findFieldValueByCode(String fieldCode) {
        return fieldValues.stream()
                .filter(fv -> fv.matches(fieldCode))
                .findFirst();
    }

    /**
     * 判断指定字段是否已有值.
     *
     * @param fieldCode 字段编码
     * @return true 如果已填写
     */
    public boolean isFieldFilled(String fieldCode) {
        return fieldValues.stream()
                .anyMatch(fv -> fv.matches(fieldCode)
                        && fv.getFieldValue() != null
                        && !fv.getFieldValue().trim().isEmpty());
    }

    /**
     * 判断评估是否已完成（状态 >= COMPLETED）.
     *
     * @return true 如果已完成
     */
    public boolean isCompleted() {
        return status.getCode() >= MonitoringStatus.COMPLETED.getCode();
    }

    /**
     * 判断评估是否已审核.
     *
     * @return true 如果已审核
     */
    public boolean isAudited() {
        return status == MonitoringStatus.AUDITED;
    }

    // ========== Getter方法 ==========

    @Override
    public CrfAssessmentId getId() {
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
     * 获取CRF模板ID.
     *
     * @return CrfTemplateId
     */
    public CrfTemplateId getCrfId() {
        return crfId;
    }

    /**
     * 获取CRF版本ID.
     *
     * @return CrfVersionId
     */
    public CrfVersionId getCrfVersionId() {
        return crfVersionId;
    }

    /**
     * 获取所属受试者阶段ID.
     *
     * @return SubjectStageId
     */
    public SubjectStageId getSubjectsStageId() {
        return subjectsStageId;
    }

    /**
     * 获取监控状态.
     *
     * @return MonitoringStatus
     */
    public MonitoringStatus getStatus() {
        return status;
    }

    /**
     * 获取完整性.
     *
     * @return Completeness
     */
    public Completeness getCompleteness() {
        return completeness;
    }

    /**
     * 是否不良事件.
     *
     * @return true 如果为不良事件
     */
    public boolean isAdverseEvent() {
        return adverseEvent;
    }

    /**
     * 设置不良事件标志.
     *
     * @param adverseEvent 是否不良事件
     */
    public void setAdverseEvent(boolean adverseEvent) {
        this.adverseEvent = adverseEvent;
    }

    /**
     * 获取评估分数.
     *
     * @return AssessmentScore
     */
    public AssessmentScore getAssessmentScore() {
        return assessmentScore;
    }

    /**
     * 获取字段值列表（不可修改视图）.
     *
     * @return 不可修改的CrfFieldValue列表
     */
    public List<CrfFieldValue> getFieldValues() {
        return fieldValues != null
                ? Collections.unmodifiableList(fieldValues)
                : Collections.emptyList();
    }

    @Override
    public String toString() {
        return "CrfAssessment{"
                + "id=" + id
                + ", crfId=" + crfId
                + ", status=" + status
                + ", completeness=" + completeness
                + '}';
    }
}
