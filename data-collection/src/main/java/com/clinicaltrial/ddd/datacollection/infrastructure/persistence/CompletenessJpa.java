package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.datacollection.domain.model.valueobject.Completeness} value object.
 */
@Embeddable
public class CompletenessJpa {

    @Column(name = "completeness_percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "completeness_filled_count")
    private int filledCount;

    @Column(name = "completeness_total_count")
    private int totalCount;

    public CompletenessJpa() {
    }

    public CompletenessJpa(BigDecimal percentage, int filledCount, int totalCount) {
        this.percentage = percentage;
        this.filledCount = filledCount;
        this.totalCount = totalCount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public int getFilledCount() {
        return filledCount;
    }

    public void setFilledCount(int filledCount) {
        this.filledCount = filledCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
