package com.clinicaltrial.ddd.datacollection.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * JPA embeddable mirroring the domain {@link com.clinicaltrial.ddd.datacollection.domain.model.valueobject.BaselineTime} value object.
 */
@Embeddable
public class BaselineTimeJpa {

    @Temporal(TemporalType.DATE)
    @Column(name = "baseline_date")
    private Date baselineDate;

    @Column(name = "baseline_source", length = 100)
    private String source;

    public BaselineTimeJpa() {
    }

    public BaselineTimeJpa(Date baselineDate, String source) {
        this.baselineDate = baselineDate;
        this.source = source;
    }

    public Date getBaselineDate() {
        return baselineDate;
    }

    public void setBaselineDate(Date baselineDate) {
        this.baselineDate = baselineDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
