package com.clinicaltrial.ddd.trial.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * JPA entity mirroring the {@link com.clinicaltrial.ddd.trial.domain.model.entity.CrfFieldOption} domain entity.
 */
@Entity
@Table(name = "rd_crf_field_option")
public class CrfFieldOptionJpaEntity {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "option_label", nullable = false, length = 200)
    private String optionLabel;

    @Column(name = "option_value", nullable = false, length = 100)
    private String optionValue;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "score", precision = 10, scale = 2)
    private BigDecimal score;

    public CrfFieldOptionJpaEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOptionLabel() {
        return optionLabel;
    }

    public void setOptionLabel(String optionLabel) {
        this.optionLabel = optionLabel;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public void setOptionValue(String optionValue) {
        this.optionValue = optionValue;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }
}
