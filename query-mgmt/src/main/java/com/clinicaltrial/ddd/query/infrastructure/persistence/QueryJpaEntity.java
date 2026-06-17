package com.clinicaltrial.ddd.query.infrastructure.persistence;

import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryStatus;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryType;
import com.clinicaltrial.ddd.query.domain.model.valueobject.QueryUpdateType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * JPA entity mirroring the Query aggregate root.
 * Maps to the rd_query table.
 */
@Entity
@Table(name = "rd_query")
public class QueryJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "assessment_id")
    private Long assessmentId;

    @Column(name = "field_code", length = 100)
    private String fieldCode;

    @Column(name = "sub_table_id", length = 100)
    private String subTableId;

    @Column(name = "field_type", length = 50)
    private String fieldType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private QueryStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private QueryType type;

    @Column(name = "question", length = 2000)
    private String question;

    @Column(name = "response", length = 2000)
    private String response;

    @Enumerated(EnumType.STRING)
    @Column(name = "update_type", length = 30)
    private QueryUpdateType updateType;

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "original_field_code", length = 100)
    private String originalFieldCode;

    @Column(name = "original_field_label", length = 200)
    private String originalFieldLabel;

    @Column(name = "original_field_value", length = 2000)
    private String originalFieldValue;

    @Column(name = "original_field_value_text", length = 2000)
    private String originalFieldValueText;

    @Column(name = "original_snapshot_at")
    private Date originalSnapshotAt;

    @Column(name = "current_field_code", length = 100)
    private String currentFieldCode;

    @Column(name = "current_field_label", length = 200)
    private String currentFieldLabel;

    @Column(name = "current_field_value", length = 2000)
    private String currentFieldValue;

    @Column(name = "current_field_value_text", length = 2000)
    private String currentFieldValueText;

    @Column(name = "current_snapshot_at")
    private Date currentSnapshotAt;

    public QueryJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getFieldCode() {
        return fieldCode;
    }

    public void setFieldCode(String fieldCode) {
        this.fieldCode = fieldCode;
    }

    public String getSubTableId() {
        return subTableId;
    }

    public void setSubTableId(String subTableId) {
        this.subTableId = subTableId;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public QueryStatus getStatus() {
        return status;
    }

    public void setStatus(QueryStatus status) {
        this.status = status;
    }

    public QueryType getType() {
        return type;
    }

    public void setType(QueryType type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public QueryUpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(QueryUpdateType updateType) {
        this.updateType = updateType;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getOriginalFieldCode() {
        return originalFieldCode;
    }

    public void setOriginalFieldCode(String originalFieldCode) {
        this.originalFieldCode = originalFieldCode;
    }

    public String getOriginalFieldLabel() {
        return originalFieldLabel;
    }

    public void setOriginalFieldLabel(String originalFieldLabel) {
        this.originalFieldLabel = originalFieldLabel;
    }

    public String getOriginalFieldValue() {
        return originalFieldValue;
    }

    public void setOriginalFieldValue(String originalFieldValue) {
        this.originalFieldValue = originalFieldValue;
    }

    public String getOriginalFieldValueText() {
        return originalFieldValueText;
    }

    public void setOriginalFieldValueText(String originalFieldValueText) {
        this.originalFieldValueText = originalFieldValueText;
    }

    public Date getOriginalSnapshotAt() {
        return originalSnapshotAt;
    }

    public void setOriginalSnapshotAt(Date originalSnapshotAt) {
        this.originalSnapshotAt = originalSnapshotAt;
    }

    public String getCurrentFieldCode() {
        return currentFieldCode;
    }

    public void setCurrentFieldCode(String currentFieldCode) {
        this.currentFieldCode = currentFieldCode;
    }

    public String getCurrentFieldLabel() {
        return currentFieldLabel;
    }

    public void setCurrentFieldLabel(String currentFieldLabel) {
        this.currentFieldLabel = currentFieldLabel;
    }

    public String getCurrentFieldValue() {
        return currentFieldValue;
    }

    public void setCurrentFieldValue(String currentFieldValue) {
        this.currentFieldValue = currentFieldValue;
    }

    public String getCurrentFieldValueText() {
        return currentFieldValueText;
    }

    public void setCurrentFieldValueText(String currentFieldValueText) {
        this.currentFieldValueText = currentFieldValueText;
    }

    public Date getCurrentSnapshotAt() {
        return currentSnapshotAt;
    }

    public void setCurrentSnapshotAt(Date currentSnapshotAt) {
        this.currentSnapshotAt = currentSnapshotAt;
    }
}
