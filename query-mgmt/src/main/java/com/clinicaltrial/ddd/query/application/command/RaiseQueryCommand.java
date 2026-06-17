package com.clinicaltrial.ddd.query.application.command;

import com.clinicaltrial.ddd.datacollection.domain.model.valueobject.CrfAssessmentId;

import java.util.Objects;

/**
 * RaiseQueryCommand — 提出质疑指令.
 * <p>
 * 封装提出质疑所需的全部输入参数。
 * 由应用层服务处理，转换为领域操作。
 * </p>
 */
public class RaiseQueryCommand {

    private final CrfAssessmentId assessmentId;
    private final String fieldCode;
    private final String subTableId;
    private final String fieldType;
    private final String question;
    private final String originalFieldCode;
    private final String originalFieldValue;
    private final String originalFieldValueText;
    private final Long userId;

    /**
     * 构造RaiseQueryCommand.
     *
     * @param assessmentId        被质疑的CRF评估ID
     * @param fieldCode           被质疑字段编码
     * @param subTableId          子表ID（非子表字段传null）
     * @param fieldType           字段类型
     * @param question            质疑内容
     * @param originalFieldCode   质疑时字段编码（快照用）
     * @param originalFieldValue  质疑时字段值（编码值，快照用）
     * @param originalFieldValueText 质疑时字段值显示文本（快照用）
     * @param userId              提出质疑的用户ID
     */
    public RaiseQueryCommand(CrfAssessmentId assessmentId,
                              String fieldCode,
                              String subTableId,
                              String fieldType,
                              String question,
                              String originalFieldCode,
                              String originalFieldValue,
                              String originalFieldValueText,
                              Long userId) {
        this.assessmentId = Objects.requireNonNull(assessmentId, "assessmentId must not be null");
        this.fieldCode = Objects.requireNonNull(fieldCode, "fieldCode must not be null");
        this.subTableId = subTableId;
        this.fieldType = fieldType;
        this.question = Objects.requireNonNull(question, "question must not be null");
        this.originalFieldCode = Objects.requireNonNull(originalFieldCode, "originalFieldCode must not be null");
        this.originalFieldValue = originalFieldValue;
        this.originalFieldValueText = originalFieldValueText;
        this.userId = Objects.requireNonNull(userId, "userId must not be null");
    }

    /**
     * 获取被质疑的CRF评估ID.
     *
     * @return CrfAssessmentId
     */
    public CrfAssessmentId getAssessmentId() {
        return assessmentId;
    }

    /**
     * 获取被质疑字段编码.
     *
     * @return 字段编码
     */
    public String getFieldCode() {
        return fieldCode;
    }

    /**
     * 获取子表ID.
     *
     * @return 子表ID，可能为null
     */
    public String getSubTableId() {
        return subTableId;
    }

    /**
     * 获取字段类型.
     *
     * @return 字段类型，可能为null
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * 获取质疑内容.
     *
     * @return 质疑内容
     */
    public String getQuestion() {
        return question;
    }

    /**
     * 获取质疑时字段编码（用于构建快照）.
     *
     * @return 字段编码
     */
    public String getOriginalFieldCode() {
        return originalFieldCode;
    }

    /**
     * 获取质疑时字段值（编码值）.
     *
     * @return 字段值，可能为null
     */
    public String getOriginalFieldValue() {
        return originalFieldValue;
    }

    /**
     * 获取质疑时字段值显示文本.
     *
     * @return 显示文本，可能为null
     */
    public String getOriginalFieldValueText() {
        return originalFieldValueText;
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
