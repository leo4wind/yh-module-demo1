package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.aggregate.CrfTemplate} aggregate root.
 */
@Entity
@Table(name = "rd_crf_template")
public class CrfTemplateJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "default_version_id")
    private Long defaultVersionId;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "estimate_time", length = 100)
    private String estimateTime;

    @Column(name = "notice", length = 500)
    private String notice;

    @Column(name = "introduce", length = 1000)
    private String introduce;

    @OneToMany(mappedBy = "templateId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CrfFormJpaEntity> forms = new ArrayList<>();

    public CrfTemplateJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getDefaultVersionId() {
        return defaultVersionId;
    }

    public void setDefaultVersionId(Long defaultVersionId) {
        this.defaultVersionId = defaultVersionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getEstimateTime() {
        return estimateTime;
    }

    public void setEstimateTime(String estimateTime) {
        this.estimateTime = estimateTime;
    }

    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public List<CrfFormJpaEntity> getForms() {
        return forms;
    }

    public void setForms(List<CrfFormJpaEntity> forms) {
        this.forms = forms;
    }
}
